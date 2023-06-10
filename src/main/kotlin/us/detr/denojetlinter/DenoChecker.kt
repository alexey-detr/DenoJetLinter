package us.detr.denojetlinter

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project

object DenoChecker {
    private var hasShownNotification = false
    private var isDenoChecked: Boolean? = null

    fun isDenoAvailable(project: Project): Boolean {
        if (isDenoChecked == null) {
            checkDenoAvailability(project)
        }
        return isDenoChecked ?: false
    }

    private fun checkDenoAvailability(project: Project) {
        try {
            val process = ProcessBuilder(listOf("deno", "--version")).start()
            isDenoChecked = (process.waitFor() == 0)
        } catch (ex: Exception) {
            isDenoChecked = false
            if (!hasShownNotification) {
                showNotification(project)
                hasShownNotification = true
            }
        }
    }

    private fun showNotification(project: Project) {
        val notificationGroup =
            NotificationGroupManager.getInstance().getNotificationGroup("DenoJetLinterNotifications")
        val notification = notificationGroup.createNotification(
            "Deno Linting Error",
            "Deno binary not found. Please install Deno and ensure it is in your PATH.",
            NotificationType.ERROR
        )
        Notifications.Bus.notify(notification, project)
    }
}