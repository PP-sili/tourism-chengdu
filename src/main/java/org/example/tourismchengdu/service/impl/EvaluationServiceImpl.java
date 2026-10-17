package org.example.tourismchengdu.service.impl;

import org.example.tourismchengdu.common.BusinessException;
import org.example.tourismchengdu.common.ResultCode;
import org.example.tourismchengdu.constant.EvaluationConstants;
import org.example.tourismchengdu.dto.common.PageResult;
import org.example.tourismchengdu.dto.evaluation.CanEvaluateVO;
import org.example.tourismchengdu.dto.evaluation.CreateEvaluationRequest;
import org.example.tourismchengdu.dto.evaluation.EvaluationSummaryVO;
import org.example.tourismchengdu.dto.evaluation.EvaluationVO;
import org.example.tourismchengdu.dto.scene.SceneListVO;
import org.example.tourismchengdu.entity.AppUser;
import org.example.tourismchengdu.entity.Scene;
import org.example.tourismchengdu.entity.SceneEvaluation;
import org.example.tourismchengdu.mapper.AppUserMapper;
import org.example.tourismchengdu.mapper.OrderMapper;
import org.example.tourismchengdu.mapper.SceneEvaluationMapper;
import org.example.tourismchengdu.mapper.SceneMapper;
import org.example.tourismchengdu.service.EvaluationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class EvaluationServiceImpl implements EvaluationService {

    private static final int MAX_PAGE_SIZE = 50;

    private final SceneEvaluationMapper sceneEvaluationMapper;
    private final SceneMapper sceneMapper;
    private final AppUserMapper appUserMapper;
    private final OrderMapper orderMapper;

    public EvaluationServiceImpl(SceneEvaluationMapper sceneEvaluationMapper,
                                   SceneMapper sceneMapper,
                                   AppUserMapper appUserMapper,
                                   OrderMapper orderMapper) {
        this.sceneEvaluationMapper = sceneEvaluationMapper;
        this.sceneMapper = sceneMapper;
        this.appUserMapper = appUserMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EvaluationVO createEvaluation(Long userId, CreateEvaluationRequest request) {
        requireUser(userId);
        Scene scene = requireScene(request.getSceneId());
        assertCanEvaluate(userId, scene);

        SceneEvaluation existing = sceneEvaluationMapper.findByUserIdAndSceneId(userId, scene.getId());
        if (existing != null) {
            throw new BusinessException(ResultCode.CONFLICT, "您已评价过该景点，每个景点仅可评价一次");
        }

        SceneEvaluation evaluation = new SceneEvaluation();
        evaluation.setUserId(userId);
        evaluation.setSceneId(scene.getId());
        evaluation.setEvalType(request.getType());
        evaluation.setContent(request.getContent());
        evaluation.setRating(resolveRating(request.getType(), request.getRating()));
        evaluation.setStatus(1);
        sceneEvaluationMapper.insert(evaluation);

        return toVO(sceneEvaluationMapper.findVisibleById(evaluation.getId()));
    }

    @Override
    public PageResult<EvaluationVO> listSceneEvaluations(Long sceneId, String type, int page, int size) {
        requireScene(sceneId);
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int offset = (safePage - 1) * safeSize;

        List<SceneEvaluation> evaluations;
        long total;
        if (StringUtils.hasText(type) && !"all".equalsIgnoreCase(type)) {
            validateType(type);
            evaluations = sceneEvaluationMapper.findBySceneIdAndType(sceneId, type, offset, safeSize);
            total = sceneEvaluationMapper.countBySceneIdAndType(sceneId, type);
        } else {
            evaluations = sceneEvaluationMapper.findBySceneId(sceneId, offset, safeSize);
            total = sceneEvaluationMapper.countBySceneId(sceneId);
        }

        List<EvaluationVO> list = evaluations.stream().map(this::toVO).toList();
        return new PageResult<>(list, total, safePage, safeSize);
    }

    @Override
    public EvaluationSummaryVO getSceneEvaluationSummary(Long sceneId) {
        requireScene(sceneId);
        EvaluationSummaryVO summary = new EvaluationSummaryVO();
        summary.setGoodCount(sceneEvaluationMapper.countBySceneIdAndType(sceneId, EvaluationConstants.TYPE_GOOD));
        summary.setBadCount(sceneEvaluationMapper.countBySceneIdAndType(sceneId, EvaluationConstants.TYPE_BAD));
        summary.setSuggestionCount(sceneEvaluationMapper.countBySceneIdAndType(sceneId, EvaluationConstants.TYPE_SUGGESTION));
        summary.setTotalCount(sceneEvaluationMapper.countBySceneId(sceneId));
        return summary;
    }

    @Override
    public PageResult<EvaluationVO> listMyEvaluations(Long userId, int page, int size) {
        requireUser(userId);
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int offset = (safePage - 1) * safeSize;

        List<SceneEvaluation> evaluations = sceneEvaluationMapper.findByUserId(userId, offset, safeSize);
        long total = sceneEvaluationMapper.countByUserId(userId);
        List<EvaluationVO> list = evaluations.stream().map(this::toVO).toList();
        return new PageResult<>(list, total, safePage, safeSize);
    }

    @Override
    public CanEvaluateVO checkCanEvaluate(Long userId, Long sceneId) {
        Scene scene = requireScene(sceneId);
        CanEvaluateVO vo = new CanEvaluateVO();

        SceneEvaluation existing = sceneEvaluationMapper.findByUserIdAndSceneId(userId, sceneId);
        if (existing != null) {
            vo.setCanEvaluate(false);
            vo.setAlreadyEvaluated(true);
            vo.setReason("您已评价过该景点");
            return vo;
        }

        if (SceneListVO.isBookable(scene.getTicketPrice())) {
            if (!orderMapper.hasVisitedScene(userId, sceneId)) {
                vo.setCanEvaluate(false);
                vo.setAlreadyEvaluated(false);
                vo.setReason("需购买并游玩该景点后可评价");
                return vo;
            }
        }

        vo.setCanEvaluate(true);
        vo.setAlreadyEvaluated(false);
        vo.setReason(null);
        return vo;
    }

    private void assertCanEvaluate(Long userId, Scene scene) {
        CanEvaluateVO check = checkCanEvaluate(userId, scene.getId());
        if (!check.isCanEvaluate()) {
            throw new BusinessException(ResultCode.FORBIDDEN, check.getReason());
        }
    }

    private AppUser requireUser(Long userId) {
        AppUser user = appUserMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在");
        }
        return user;
    }

    private Scene requireScene(Long sceneId) {
        Scene scene = sceneMapper.findById(sceneId);
        if (scene == null || scene.getStatus() == null || scene.getStatus() != 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "景点不存在");
        }
        return scene;
    }

    private void validateType(String type) {
        if (!EvaluationConstants.TYPE_GOOD.equals(type)
                && !EvaluationConstants.TYPE_BAD.equals(type)
                && !EvaluationConstants.TYPE_SUGGESTION.equals(type)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "评价类型无效");
        }
    }

    private Integer resolveRating(String type, Integer rating) {
        if (rating != null) {
            return rating;
        }
        return switch (type) {
            case EvaluationConstants.TYPE_GOOD -> 5;
            case EvaluationConstants.TYPE_BAD -> 2;
            case EvaluationConstants.TYPE_SUGGESTION -> 3;
            default -> 3;
        };
    }

    private EvaluationVO toVO(SceneEvaluation evaluation) {
        AppUser user = appUserMapper.findById(evaluation.getUserId());
        EvaluationVO vo = EvaluationVO.from(evaluation, user);
        Scene scene = sceneMapper.findById(evaluation.getSceneId());
        if (scene != null) {
            vo.setSceneName(scene.getName());
        }
        return vo;
    }
}
