package com.hqup.jokes.dal;

import com.hqup.jokes.entity.Joke;

public interface ICrudDao {

	void insert(Joke joke);

	// Joke find(long id);

	void delete(Joke joke);

	void clearDB();

}
