package org.example.tourismchengdu.service;

import org.example.tourismchengdu.dto.common.PageResult;
import org.example.tourismchengdu.dto.evaluation.CanEvaluateVO;
import org.example.tourismchengdu.dto.evaluation.CreateEvaluationRequest;
import org.example.tourismchengdu.dto.evaluation.EvaluationSummaryVO;
import org.example.tourismchengdu.dto.evaluation.EvaluationVO;

public interface EvaluationService {

    EvaluationVO createEvaluation(Long userId, CreateEvaluationRequest request);

    PageResult<EvaluationVO> listSceneEvaluations(Long sceneId, String type, int page, int size);

    EvaluationSummaryVO getSceneEvaluationSummary(Long sceneId);

    PageResult<EvaluationVO> listMyEvaluations(Long userId, int page, int size);

    CanEvaluateVO checkCanEvaluate(Long userId, Long sceneId);
}
