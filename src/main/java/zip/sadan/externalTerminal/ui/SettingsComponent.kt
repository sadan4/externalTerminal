package zip.sadan.externalTerminal.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.observable.dispatcher.SingleEventDispatcher
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel

private class ObservableSetting<T>(initialValue: T) : ObservableMutableProperty<T> {
    private val dispatcher = SingleEventDispatcher.create<T>()
    private var v: T = initialValue
    override fun set(value: T) {
        v = value
        dispatcher.fireEvent(v)
    }

    override fun get(): T = v

    override fun afterChange(disposable: Disposable?, listener: (T) -> Unit) {
        dispatcher.whenEventHappened(disposable, listener)
    }

}

private fun <T> observableSetting(initialValue: T): ObservableMutableProperty<T> = ObservableSetting(initialValue)

class SettingsComponent {
    class Model {
        var _path = observableSetting("")
        var path by _path
    }

    val model = Model()

    val panel = panel {
        row("External terminal path:") {
            textFieldWithBrowseButton()
                .bindText(model._path)
        }
    }
}