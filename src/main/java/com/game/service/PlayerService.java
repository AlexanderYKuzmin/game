package com.game.service;

import com.game.entity.Player;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface PlayerService {
    List<Player> getAllByParameters(Map<String, String> parameters);
    Integer getCount(Map<String, String> parameters);
    Player createPlayer(Player player);
    Player getPlayer(Long id);
    Player updatePlayer(Player player);
    Boolean deletePlayer(Long id);
}
