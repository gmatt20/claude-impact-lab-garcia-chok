import ComposeApp
import SwiftUI
import UIKit

/// Hosts the shared Compose Multiplatform UI (composeApp) as a native UIKit view controller.
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        // Compose applies the safe-area and keyboard insets itself.
        ComposeView()
            .ignoresSafeArea()
            .background(Color("LaunchBackground"))
    }
}
