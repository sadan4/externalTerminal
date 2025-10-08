package zip.sadan.externalTerminal

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.RoamingType
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "zip.sadan.externalTerminal.Settings",
    storages = [Storage(value = "externalTerminalSettings.xml", roamingType = RoamingType.LOCAL)]
)
internal class SettingsService : PersistentStateComponent<SettingsService.State> {
    internal class State {
        var externalTerminalPath: String? = null
    }

    private var state = State()

    override fun getState(): SettingsService.State = state

    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        val instance: SettingsService
            get() = ApplicationManager
                .getApplication()
                .getService<SettingsService>(SettingsService::class.java)
    }
}
