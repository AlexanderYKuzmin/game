package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
//import org.hibernate.mapping.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @GetMapping(value = "/players")
    public ResponseEntity<List<Player>> getPlayerList(@RequestParam Map<String, String> parameters) {
        List<Player> pagedPlayers = playerService.getAllByParameters(parameters);
        return pagedPlayers != null
                    ? new ResponseEntity<>(pagedPlayers, HttpStatus.OK)
                    : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/players/count")
    public ResponseEntity<Integer> countPlayers(@RequestParam Map<String, String> parameters) {
        return new ResponseEntity<>(playerService.getCount(parameters), HttpStatus.OK);
    }

    @PostMapping(value = "/players")
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        if(!checkBodyParameters(player)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if(player.getName() == null || player.getTitle() == null || player.getRace() == null
        || player.getProfession() == null || player.getBirthday() == null || player.getExperience() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        player.setLevel(countLevel(player.getExperience()));
        player.setUntilNextLevel(countUntilNextLevel(player.getExperience(), null));

        Player createdPlayer = playerService.createPlayer(player);
        return createdPlayer != null
                ? new ResponseEntity<>(createdPlayer, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "players/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable(name = "id") Long id) {
        if(id != null && id > 0) {
            Player player = playerService.getPlayer(id);
            return player != null
                    ? new ResponseEntity<>(player, HttpStatus.OK)
                    : new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "players/{id}")
    public ResponseEntity<Player> update(@PathVariable(name = "id") Long id, @RequestBody Player player) {
        if(player == null || id == null || id <= 0 || !checkBodyParameters(player)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Player existedPlayer = playerService.getPlayer(id);
        if(existedPlayer == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if(player.getName() != null) existedPlayer.setName(player.getName());
        if(player.getTitle() != null) existedPlayer.setTitle(player.getTitle());
        if(player.getRace() != null) existedPlayer.setRace(player.getRace());
        if(player.getProfession() != null) existedPlayer.setProfession(player.getProfession());
        if(player.getBirthday() != null) existedPlayer.setBirthday(player.getBirthday());
        if(player.isBanned() != null) existedPlayer.setBanned(player.isBanned());
        if(player.getExperience() != null) {
            existedPlayer.setExperience(player.getExperience());
            existedPlayer.setLevel(countLevel(player.getExperience()));
            existedPlayer.setUntilNextLevel(countUntilNextLevel(player.getExperience(), existedPlayer.getLevel()));
        }

        Player updatedPlayer = playerService.updatePlayer(existedPlayer);
        return updatedPlayer != null
            ? new ResponseEntity<>(updatedPlayer, HttpStatus.OK)
            : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/players/{id}")
    public ResponseEntity<?> deletePlayer(@PathVariable(name = "id") Long id) {
        if(id != null && id > 0) {
            return playerService.deletePlayer(id)
                    ? new ResponseEntity<>(HttpStatus.OK)
                    : new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private Integer countLevel(Integer experience) {
        return  (int) ((Math.sqrt(2500 + (200 * experience)) - 50) * 0.01);
    }

    private Integer countUntilNextLevel(Integer experience, Integer level) {
        if(level == null) {
            return 50 * (countLevel(experience) + 1) * (countLevel(experience) + 2) - experience;
        }
        return 50 * (level + 1) * (level + 2) - experience;
    }

    private boolean checkBodyParameters(Player player) {
        String name = player.getName();
        if(name != null) {
            if(name.trim().isEmpty() || name.length() > 12) {
                return false;
            }
        }

        String title = player.getTitle();
        if(title != null) {
            if(title.isEmpty() || title.length() > 30) {
                return false;
            }
        }

        Race race = player.getRace();
        if(race != null) {

        }

        Profession profession = player.getProfession();
        if (profession != null) {

        }

        Date birthday = player.getBirthday();
        if(birthday != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(birthday);
            int year = calendar.get(Calendar.YEAR);
            if (year < 2000 || year > 3000 ) {
                return false;
            }
        }

        Boolean banned = player.isBanned();

        Integer exp = player.getExperience();
        if(exp != null) {
            if(exp < 0 || exp > 10000000) {
                return false;
            }
        }
        return true;
    }
}
