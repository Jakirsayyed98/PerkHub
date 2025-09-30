package app.perkhub.room

import android.content.Context
import app.perkhub.Screens.Home.Model.Notification.Data
import kotlinx.coroutines.flow.Flow

class NotificationRepository(context: Context) {
    private val notificationDao = HomeDatabase.getInstance(context).notificationDao()

    // Get notifications as Flow for real-time updates
    fun getNotificationsFlow(): Flow<List<Data>> {
        return notificationDao.getAllNotifications()
    }

    // Get notifications once
    suspend fun getNotifications(): List<Data> {
        return notificationDao.getAllNotificationsOnce()
    }

    // Save notifications to database
    suspend fun saveNotifications(notifications: List<Data>) {
        notificationDao.insertNotifications(notifications)
    }

    // Save single notification (for push notifications)
    suspend fun saveNotification(notification: Data) {
        notificationDao.insertNotification(notification)
    }

    // Clear all notifications
    suspend fun clearNotifications() {
        notificationDao.clearAll()
    }

    // Check if database has data
    suspend fun hasNotifications(): Boolean {
        return notificationDao.getCount() > 0
    }

    // Delete single notification
    suspend fun deleteNotification(notificationId: String) {
        notificationDao.deleteNotification(notificationId)
    }
}