import 'package:camera/camera.dart';

class FlashHelper {
  static Future<bool> isFlashActive() async {
    List<CameraDescription> cameras = await availableCameras();
    CameraController controller = CameraController(cameras[0], ResolutionPreset.low);
    await controller.initialize();
    return controller.value.flashMode == FlashMode.torch;
  }
}
