package zip.sadan.externalTerminal

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import java.io.File

private val LOG = logger<OpenExternalTerminalAction>()

private fun fallbackHomeDir(project: Project?): File {
    val homeDir: String? = System.getProperty("user.home") ?: System.getenv("HOME")
    if (homeDir == null) {
        Messages.showErrorDialog(project, "Could not find home directory", "External Terminal")
        error("Could not find home directory")
    }
    return File(homeDir)
}

fun noti(msg: String, level: NotificationType): Notification {
    return Notification("zip.sadan.externalTerminal.warnings", "External Terminal", msg, level)
}

fun notiWarn(msg: String) = noti(msg, NotificationType.WARNING)

fun notiErr(msg: String) = noti(msg, NotificationType.ERROR)


class OpenExternalTerminalAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val curProj = e.project
        val f = curProj?.workspaceFile
        val rootDir = when (f?.extension) {
            "iws" -> {
                f.parent
            }

            "xml" -> {
                f.parent?.parent
            }

            else -> {
                null
            }
        }
            ?.toNioPath()
            ?.toFile() ?: run {
            Notifications.Bus.notify(notiWarn("Could not find project root directory"), curProj)
            fallbackHomeDir(curProj)
        }
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            Notifications.Bus.notify(notiWarn("Project root directory does not exist $rootDir"), curProj)
            return
        }
        val terminalPath = SettingsService.instance.state.externalTerminalPath
        if (terminalPath == null || terminalPath.isBlank()) {
            Notifications.Bus.notify(notiErr("Terminal path is not set").addAction(NotificationAction.createSimple("Open Settings") {
                ShowSettingsUtil
                    .getInstance()
                    .showSettingsDialog(curProj, SettingsConfigurable::class.java)
            }))
            return
        }
        if (terminalPath.contains('/') && !File(terminalPath).isFile()) {
            Notifications.Bus.notify(notiErr("Terminal path is not a file: $terminalPath"))
            return
        }
        try {
            Runtime.getRuntime().exec(arrayOf(terminalPath, rootDir.absolutePath))
        } catch (e: Throwable) {
            LOG.error("Failed to open terminal", e)
            Notifications.Bus.notify(notiErr("Failed to open terminal: ${e.message}"), curProj)
        }

    }
}