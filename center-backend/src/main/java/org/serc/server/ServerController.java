package org.serc.server;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.serc.network.controller.dto.CveEntry;
import org.serc.network.controller.dto.HostListDto;
import org.serc.network.controller.dto.NetworkListDto;
import org.serc.network.model.Host;
import org.serc.network.model.HostVulnerability;
import org.serc.network.model.Network;
import org.serc.network.model.NetworkScheduleTask;
import org.serc.network.model.Sensor;
import org.serc.network.support.NetworkScheduleTaskRepository;
import org.serc.network.support.NetworkUtils;
import org.serc.server.support.HostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

@RestController
@RequestMapping("/server/{network}")
public class ServerController {
    
    @Autowired NetworkScheduleTaskRepository networkScheduleTaskRepository;
    @Autowired HostService hostService;
    
    @GetMapping("/overview")
    public NetworkListDto network(Network network) {
        NetworkListDto networkListDto = new NetworkListDto(network);
        List<org.serc.server.model.Host> hosts = hostService.hosts();
        networkListDto.setHostCount(hosts.size());
        return networkListDto;
    }
    
    @GetMapping("/scores")
    public Map<Long, Map<String, Object>> scores(Network network) {
        return networkScheduleTaskRepository.findByNetworkOrderByIdDesc(network).stream()
                .collect(Collectors.toMap(n -> n.getCreatedTime().getTime(), NetworkScheduleTask::scores));
    }
    
    @GetMapping("/vulnerabilities")
    public List<CveEntry> vulnerabilities(Network network) {
        List<CveEntry> cves = Lists.newArrayList();
        NetworkUtils.setCveEntries(network, NetworkUtils.getCves(network));
        for(Sensor sensor: network.getSensors()) {
            for(Host host: sensor.getHosts()) {
                HostListDto hostDto = new HostListDto(host);
                for(HostVulnerability hostVulnerability: host.getVulnerabilities()) {
                    for(CveEntry vulnerability: hostVulnerability.getCveList()) {
                        hostDto.setScore(hostDto.getScore() + vulnerability.getCvssScore());
                        hostDto.setVulnerabilityCount(hostDto.getVulnerabilityCount() + 1);
                        vulnerability.setHost(hostDto);
                        cves.add(vulnerability);
                    }
                }
            }
        }
        Collections.sort(cves);
        return cves;
    }
    
}