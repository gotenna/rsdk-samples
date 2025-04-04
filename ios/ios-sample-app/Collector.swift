import RSDK

// Callback/Listener modified implementation from https://stackoverflow.com/questions/64175099/listen-to-kotlin-coroutine-flow-from-ios
class Collector<T>: Kotlinx_coroutines_coreFlowCollector {
    
    let callback:(T) -> Void
    
    init(callback: @escaping (T) -> Void) {
        self.callback = callback
    }
    
    func emit(value: Any?) async throws {
        guard let value = value as? T else { return }
        callback(value)
    }
    
}
