package com.github.funnygopher.crowddj.database;

public class AbstractDao<T extends Dao> {

    private Long mId;

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }
}
