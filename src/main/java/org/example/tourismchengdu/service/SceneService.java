package org.example.tourismchengdu.service;

import org.example.tourismchengdu.dto.scene.SceneDetailVO;
import org.example.tourismchengdu.dto.scene.SceneListVO;
import org.example.tourismchengdu.dto.scene.TicketStatItemVO;
import org.example.tourismchengdu.dto.scene.VisitorStatItemVO;

import java.util.List;

public interface SceneService {

    List<SceneListVO> listScenes();

    SceneDetailVO getSceneDetail(Long id);

    List<VisitorStatItemVO> getVisitorStats(Long id);

    List<TicketStatItemVO> getTicketStats(Long id);
}
