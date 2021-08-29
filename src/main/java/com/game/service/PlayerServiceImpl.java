package com.game.service;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService{

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public List<Player> getAllByParameters(Map<String, String> parameters) {
        Pageable pageable = getPageable(parameters);
        if(parameters.size() > 0) {
            List<Player> players = playerRepository.findAllByParameters(parameters);
            return getPage(players, pageable);
        }
        return playerRepository.findAll(pageable).getContent();
    }

    @Override
    public Integer getCount(Map<String, String> parameters) {
        return playerRepository.findAllByParameters(parameters).size();
    }

    @Override
    public Player createPlayer(Player player) {
        return playerRepository.saveAndFlush(player);
    }

    @Override
    public Player getPlayer(Long id) {
        if(playerRepository.existsById(id)) {
            Optional<Player> optionalPlayer = playerRepository.findById(id);
            return optionalPlayer.get();
        }
        return null;
    }

    @Override
    public Player updatePlayer(Player player) {
        return playerRepository.saveAndFlush(player);
    }

    @Override
    public Boolean deletePlayer(Long id) {
        if(playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private Pageable getPageable(Map<String, String> parameters) {
        String pageNumberParam = parameters.get("pageNumber");
        String pageSizeParam = parameters.get("pageSize");
        int pageNumber = 0, pageSize = 3;
        if(pageNumberParam != null) {
            pageNumber = Integer.parseInt(pageNumberParam);
        }
        if(pageSizeParam != null) {
            pageSize = Integer.parseInt(pageSizeParam);
        }
        return PageRequest.of(pageNumber, pageSize);
    }

    private List<Player> getPage(List<Player> players, Pageable pageable) {
        if(players == null) return null;
        List<Player> pagedPlayers = new ArrayList<>();
        int startExtraction = pageable.getPageNumber() * pageable.getPageSize();
        int limitExtraction = startExtraction + pageable.getPageSize() > players.size()
                ? players.size()
                : startExtraction + pageable.getPageSize();
        for(int i = startExtraction; i < limitExtraction; i++) {
            pagedPlayers.add(players.get(i));
        }
        return pagedPlayers;
    }
}
