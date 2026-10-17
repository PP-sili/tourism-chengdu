package org.example.tourismchengdu.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.tourismchengdu.common.Result;
import org.example.tourismchengdu.dto.common.PageResult;
import org.example.tourismchengdu.dto.evaluation.EvaluationSummaryVO;
import org.example.tourismchengdu.dto.evaluation.EvaluationVO;
import org.example.tourismchengdu.dto.scene.SceneDetailVO;
import org.example.tourismchengdu.dto.scene.SceneListVO;
import org.example.tourismchengdu.dto.scene.TicketStatItemVO;
import org.example.tourismchengdu.dto.scene.VisitorStatItemVO;
import org.example.tourismchengdu.service.EvaluationService;
import org.example.tourismchengdu.service.SceneService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scenes")
@Tag(name = "景点", description = "景点列表、详情与统计")
@SecurityRequirements
public class SceneController {

    private final SceneService sceneService;
    private final EvaluationService evaluationService;

    public SceneController(SceneService sceneService, EvaluationService evaluationService) {
        this.sceneService = sceneService;
        this.evaluationService = evaluationService;
    }

    @GetMapping
    public Result<List<SceneListVO>> listScenes() {
        return Result.success(sceneService.listScenes());
    }

    @GetMapping("/{id}")
    public Result<SceneDetailVO> getSceneDetail(@PathVariable Long id) {
        return Result.success(sceneService.getSceneDetail(id));
    }

    @GetMapping("/{id}/visitor-stats")
    public Result<List<VisitorStatItemVO>> getVisitorStats(@PathVariable Long id) {
        return Result.success(sceneService.getVisitorStats(id));
    }

    @GetMapping("/{id}/ticket-stats")
    public Result<List<TicketStatItemVO>> getTicketStats(@PathVariable Long id) {
        return Result.success(sceneService.getTicketStats(id));
    }

    @GetMapping("/{id}/evaluations")
    public Result<PageResult<EvaluationVO>> listEvaluations(
            @PathVariable Long id,
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(evaluationService.listSceneEvaluations(id, type, page, size));
    }

    @GetMapping("/{id}/evaluation-summary")
    public Result<EvaluationSummaryVO> getEvaluationSummary(@PathVariable Long id) {
        return Result.success(evaluationService.getSceneEvaluationSummary(id));
    }
}
