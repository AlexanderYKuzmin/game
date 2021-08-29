package com.game.repository;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.sql.Date;
import java.util.*;

public class PlayerRepositoryCustomImpl implements PlayerRepositoryCustom{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Player> findAllByParameters(Map<String, String> filterData) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Player> query = cb.createQuery(Player.class);
        Root<Player> root = query.from(Player.class);
        List<Predicate> predicates = new ArrayList<>();

        // get name predicate
        String nameParam = filterData.get("name");
        if(nameParam != null) {
            predicates.add(cb.like(root.get("name"), "%" + nameParam + "%"));
        }

        String titleParam = filterData.get("title");
        if(titleParam != null) {
            predicates.add(cb.like(root.get("title"), "%" + titleParam + "%"));
        }

        // get Race predicate
        String raceParam = filterData.get("race");
        if(raceParam != null) {
            Race race = Race.valueOf(raceParam);
            predicates.add(cb.equal(root.get("race"), race));
        }

        // get profession predicate
        String professionParam = filterData.get("profession");
        if(professionParam != null) {
            Profession profession = Profession.valueOf(professionParam);
            predicates.add(cb.equal(root.get("profession"), profession));
        }

        // get birthday predicate two predicates
        String afterParam = filterData.get("after");
        String beforeParam = filterData.get("before");
        if(afterParam != null) {
            long after = Long.valueOf(afterParam);
            predicates.add(cb.greaterThanOrEqualTo(root.get("birthday"), new Date(after)));
        }
        if(beforeParam != null) {
            long before = Long.valueOf(beforeParam);
            predicates.add(cb.lessThanOrEqualTo(root.get("birthday"), new Date(before)));
        }

        //get banned predicate
        String bannedParam = filterData.get("banned");
        if(bannedParam != null) {
            boolean banned = Boolean.valueOf(bannedParam);
            if(banned)  predicates.add(cb.isTrue(root.get("banned")));
            else predicates.add(cb.isFalse(root.get("banned")));
        }

        //get experience predicate
        String minExpParam = filterData.get("minExperience");
        String maxExpParam = filterData.get("maxExperience");
        if(minExpParam != null) {
            int minExp = Integer.parseInt(minExpParam);
            predicates.add(cb.greaterThanOrEqualTo(root.get("experience"), minExp));
        }
        if(maxExpParam != null) {
            int maxExp = Integer.parseInt(maxExpParam);
            predicates.add(cb.lessThanOrEqualTo(root.get("experience"), maxExp));
        }

        //get level predicate
        String minLevelParam = filterData.get("minLevel");
        String maxLevelParam = filterData.get("maxLevel");
        if(minLevelParam != null || maxLevelParam != null) {
            int minLevel = minLevelParam == null ? 0 : Integer.parseInt(minLevelParam);
            int maxLevel = maxLevelParam == null ? 11000000 : Integer.parseInt(maxLevelParam);
            predicates.add(cb.between(root.get("level"), minLevel, maxLevel));
        }

        String orderParam = filterData.get("order");
        Order order;
        if(orderParam != null) {
            PlayerOrder orderEnum = PlayerOrder.valueOf(orderParam);
            order = cb.asc(root.get(orderEnum.getFieldName()));
        } else order = cb.asc(root.get("id"));

        query.select(root)
                .where(cb.and(predicates.toArray(new Predicate[predicates.size()])))
                .orderBy(order)
        ;

        List<Player> players = entityManager.createQuery(query).getResultList();

        return players;
    }
}
