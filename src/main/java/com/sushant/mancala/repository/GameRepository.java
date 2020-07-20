package com.sushant.mancala.repository;

import com.sushant.mancala.domain.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/** Data repository for storing games in Mango db. */
@Repository
public interface GameRepository extends MongoRepository<Game, String> {}
