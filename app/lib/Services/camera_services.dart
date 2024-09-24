import 'package:camera/camera.dart';

class CameraService {
  CameraService._privateConstructor();

  static final CameraService instance = CameraService._privateConstructor();

  late CameraDescription firstCamera;

  Future<void> initialize() async {
    final cameras = await availableCameras();
    firstCamera = cameras.first;
  }
}