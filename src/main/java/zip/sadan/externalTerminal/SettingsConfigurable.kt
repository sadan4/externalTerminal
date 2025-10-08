package zip.sadan.externalTerminal

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.util.NlsContexts
import zip.sadan.externalTerminal.ui.SettingsComponent
import javax.swing.JComponent

class SettingsConfigurable : Configurable {
    private val component by lazy(::SettingsComponent)

    override fun getDisplayName(): @NlsContexts.ConfigurableName String {
        return ""
    }

    override fun createComponent(): JComponent {
        return component.panel
    }

    override fun isModified(): Boolean {
        val state = SettingsService.instance.state

        return component.model.path != state.externalTerminalPath
    }

    override fun apply() {
        val state = SettingsService.instance.state
        state.externalTerminalPath = component.model.path
    }

    override fun reset() {
        val state: SettingsService.State = SettingsService.instance.state

        component.model.path = state.externalTerminalPath ?: ""
    }
}
