package com.timezy.widget.data.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.timezy.widget.data.model.CountdownEvent;
import com.timezy.widget.data.model.EventCategory;
import com.timezy.widget.data.model.EventTheme;
import com.timezy.widget.data.model.RecurrenceType;

@Entity(tableName = "countdown_events")
public class EventEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String title;
    public String description;
    public long targetDateTimeEpochMillis;
    public boolean hasSpecificTime;
    public String category;
    public String theme;
    public boolean isFavorite;
    public String recurrence;
    public int reminderMinutesBefore;
    public boolean isNotificationEnabled;
    public long createdAtEpochMillis;
    public long updatedAtEpochMillis;

    public EventEntity() {
    }

    @androidx.room.Ignore
    public EventEntity(
            long id,
            String title,
            String description,
            long targetDateTimeEpochMillis,
            boolean hasSpecificTime,
            String category,
            String theme,
            boolean isFavorite,
            String recurrence,
            int reminderMinutesBefore,
            boolean isNotificationEnabled,
            long createdAtEpochMillis,
            long updatedAtEpochMillis
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.targetDateTimeEpochMillis = targetDateTimeEpochMillis;
        this.hasSpecificTime = hasSpecificTime;
        this.category = category;
        this.theme = theme;
        this.isFavorite = isFavorite;
        this.recurrence = recurrence;
        this.reminderMinutesBefore = reminderMinutesBefore;
        this.isNotificationEnabled = isNotificationEnabled;
        this.createdAtEpochMillis = createdAtEpochMillis;
        this.updatedAtEpochMillis = updatedAtEpochMillis;
    }

    public CountdownEvent toDomain() {
        long effectiveCreated = createdAtEpochMillis > 0L ? createdAtEpochMillis : (updatedAtEpochMillis > 0L ? updatedAtEpochMillis : 0L);
        return new CountdownEvent(
                id,
                title != null ? title : "",
                description != null ? description : "",
                targetDateTimeEpochMillis,
                hasSpecificTime,
                EventCategory.Companion.fromId(category != null ? category : "other"),
                EventTheme.Companion.fromId(theme != null ? theme : "cosmic_purple"),
                isFavorite,
                RecurrenceType.Companion.fromName(recurrence != null ? recurrence : "NONE"),
                reminderMinutesBefore,
                isNotificationEnabled,
                effectiveCreated,
                updatedAtEpochMillis > 0L ? updatedAtEpochMillis : effectiveCreated
        );
    }

    public static EventEntity fromDomain(CountdownEvent event) {
        return new EventEntity(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getTargetDateTimeEpochMillis(),
                event.getHasSpecificTime(),
                event.getCategory().getId(),
                event.getTheme().getId(),
                event.isFavorite(),
                event.getRecurrence().name(),
                event.getReminderMinutesBefore(),
                event.isNotificationEnabled(),
                event.getCreatedAtEpochMillis(),
                event.getUpdatedAtEpochMillis()
        );
    }
}
