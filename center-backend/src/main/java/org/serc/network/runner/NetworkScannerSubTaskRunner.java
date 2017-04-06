package org.serc.network.runner;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.FileUtils;
import org.serc.algorithm.model.AlgorithmTask.Status;
import org.serc.network.model.NetworkScannerSubTask;
import org.serc.network.support.NetworkScannerSubTaskRepository;
import org.serc.network.support.SensorService;
import org.serc.utils.AlgorithmUtils;
import org.serc.utils.DockerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.WaitContainerResultCallback;

@Component
public class NetworkScannerSubTaskRunner {
    
    @Autowired ApplicationContext applicationContext;
    @Autowired NetworkScannerSubTaskRepository  networkScannerSubTaskRepository;
    @Autowired SensorService sensorService;
    private static final String image = "registry.cn-hangzhou.aliyuncs.com/serc/agbot:net-scanner-local";
    private static final String containerDataDir = "/data";
    
    @Async
    public void run(NetworkScannerSubTask task) {
        try {
            task.setStatus(Status.running);
            networkScannerSubTaskRepository.saveAndFlush(task);
            DockerClient dockerClient = DockerClientBuilder.getInstance(DefaultDockerClientConfig.createDefaultConfigBuilder()
                    .withDockerHost(task.getTask().getSensor().getDockerApi())).build();
            task.setContainerId(initContainer(dockerClient, task));
            networkScannerSubTaskRepository.saveAndFlush(task);
            runContainer(dockerClient, task);
            handleResult(dockerClient, task);
            task.setStatus(Status.success);
            networkScannerSubTaskRepository.saveAndFlush(task);
        } catch (Exception e) {
            task.setStatus(Status.failure);
            task.setErrorStack(AlgorithmUtils.getErrorStackString(e));
            networkScannerSubTaskRepository.saveAndFlush(task);
        }catch (Error e) {
            task.setStatus(Status.failure);
            task.setErrorStack(AlgorithmUtils.getErrorStackString(e));
            networkScannerSubTaskRepository.saveAndFlush(task);
        } finally {
            task.getTask().getSubTasks().stream()
                .filter(st -> Status.created.equals(st.getStatus()))
                .findFirst().ifPresent(st -> applicationContext.getBean(NetworkScannerSubTaskRunner.class).run(st));
        }
    }
    
    private String initContainer(DockerClient dockerClient, NetworkScannerSubTask task) {
        return dockerClient.createContainerCmd(image)
                .withEnv("dataDir=" + containerDataDir, 
                        "dockerHost=" + task.getTask().getSensor().getDockerApi(),
                        "ips=" + task.getIp())
                .exec().getId();
    }
    
    private void runContainer(DockerClient dockerClient, NetworkScannerSubTask task) {
        task.setStartTime(new Date());
        dockerClient.startContainerCmd(task.getContainerId()).exec();
        dockerClient.waitContainerCmd(task.getContainerId()).exec(new WaitContainerResultCallback()).awaitStatusCode();
        task.setEndTime(new Date());
    }
    
    private void handleResult(DockerClient dockerClient, NetworkScannerSubTask task) throws IOException, ArchiveException {
        File tmpDir = new File(org.serc.ApplicationContext.hostTmpDir, task.getContainerId());
        DockerUtils.copyFiles(dockerClient, task.getContainerId(), containerDataDir, tmpDir);
        String output = FileUtils.readFileToString(new File(tmpDir, "output"), "utf-8");
        sensorService.parseVulnerabilities(task.getTask().getSensor(), output);
    }

}