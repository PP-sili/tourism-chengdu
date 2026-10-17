package org.example.tourismchengdu.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.tourismchengdu.common.Result;
import org.example.tourismchengdu.dto.common.PageResult;
import org.example.tourismchengdu.dto.evaluation.CanEvaluateVO;
import org.example.tourismchengdu.dto.evaluation.CreateEvaluationRequest;
import org.example.tourismchengdu.dto.evaluation.EvaluationVO;
import org.example.tourismchengdu.security.UserContext;
import org.example.tourismchengdu.service.EvaluationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/evaluations")
@Tag(name = "景点评价", description = "好评/差评/建议类评价")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping
    public Result<EvaluationVO> createEvaluation(@Valid @RequestBody CreateEvaluationRequest request) {
        return Result.success(evaluationService.createEvaluation(UserContext.getUserId(), request));
    }

    @GetMapping("/mine")
    public Result<PageResult<EvaluationVO>> listMyEvaluations(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(evaluationService.listMyEvaluations(UserContext.getUserId(), page, size));
    }

    @GetMapping("/can-evaluate")
    public Result<CanEvaluateVO> checkCanEvaluate(@RequestParam Long sceneId) {
        return Result.success(evaluationService.checkCanEvaluate(UserContext.getUserId(), sceneId));
    }
}
