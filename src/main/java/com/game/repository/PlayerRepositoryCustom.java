package com.game.repository;

import com.game.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface PlayerRepositoryCustom {
   // List<Player> findAllByParameters(Map<String, String> filterData, Pageable pageable);
    List<Player> findAllByParameters(Map<String, String> filterData);
}
