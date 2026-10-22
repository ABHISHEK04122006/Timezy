package com.timezy.widget.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM countdown_events ORDER BY targetDateTimeEpochMillis ASC")
    LiveData<List<EventEntity>> getAllEventsLiveData();

    @Query("SELECT * FROM countdown_events ORDER BY targetDateTimeEpochMillis ASC")
    List<EventEntity> getAllEventsSync();

    @Query("SELECT * FROM countdown_events WHERE id = :id LIMIT 1")
    LiveData<EventEntity> getEventByIdLiveData(long id);

    @Query("SELECT * FROM countdown_events WHERE id = :id LIMIT 1")
    EventEntity getEventByIdSync(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertEvent(EventEntity event);

    @Update
    void updateEvent(EventEntity event);

    @Delete
    void deleteEvent(EventEntity event);

    @Query("DELETE FROM countdown_events WHERE id = :id")
    int deleteEventById(long id);

    @Query("UPDATE countdown_events SET isFavorite = :isFavorite WHERE id = :id")
    int updateFavorite(long id, boolean isFavorite);

    @Query("DELETE FROM countdown_events")
    int clearAllEvents();
}
