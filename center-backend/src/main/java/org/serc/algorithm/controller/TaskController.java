package org.serc.algorithm.controller;

import org.serc.algorithm.controller.dto.AlgorithmTaskDto;
import org.serc.algorithm.controller.dto.AlgorithmTaskListDto;
import org.serc.algorithm.model.AlgorithmTask;
import org.serc.algorithm.support.AlgorithmServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    
    @Autowired
    private AlgorithmServiceImpl algorithmTaskService;
    
    @PostMapping("/{task}/rerun")
    public AlgorithmTaskDto rerun(@PathVariable AlgorithmTask task) {
        return new AlgorithmTaskDto(algorithmTaskService.run(task.getAlgorithm(), task.getInput()));
    }
    
    @GetMapping("/{task}")
    public AlgorithmTaskDto getOne(@PathVariable AlgorithmTask task) {
        return new AlgorithmTaskDto(task);
    }
    
    @GetMapping("/{task}/wait")
    public AlgorithmTaskDto wait(@PathVariable AlgorithmTask task) {
        return new AlgorithmTaskDto(algorithmTaskService.wait(task));
    }
    
    @GetMapping("")
    public Page<AlgorithmTaskListDto> tasks(@PageableDefault(sort = "createdTime", direction = Direction.DESC) Pageable pageable) {
        return algorithmTaskService.getTasks(pageable).map(AlgorithmTaskListDto::new);
    }
}
