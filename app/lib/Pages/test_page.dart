import 'dart:async';
import 'dart:typed_data';

import 'package:app/Pages/avatar_generator.dart';
import 'package:app/Pages/camera_page.dart';
import 'package:app/Pages/question1_page.dart';
import 'package:app/Pages/question2_page.dart';
import 'package:app/Pages/question3_page.dart';
import 'package:app/Pages/question4_page.dart';
import 'package:app/Pages/question5_page.dart';
import 'package:app/Pages/question_page.dart';
import 'package:app/Pages/websocket_test.dart';
import 'package:app/Services/camera_services.dart';
import 'package:avataaars/avataaars.dart';
import 'package:flutter/material.dart';
import 'package:noise_meter/noise_meter.dart';
import 'package:palette_generator/palette_generator.dart';
import 'package:torchx/torchx.dart';
import 'package:vibration/vibration.dart';

class TestPage extends StatefulWidget {
  @override
  _TestPageState createState() => _TestPageState();
}

class _TestPageState extends State<TestPage> {
  bool _isFlashActive = false;
  int _counter = 0;
  bool _isRecording = false;
  NoiseReading _latestReading = NoiseReading([0, 0]);
  StreamSubscription<NoiseReading>? _noiseSubscription;
  late NoiseMeter _noiseMeter;
  PaletteGenerator? paletteGenerator;
  Color? face = new Color(0xFFFFFFFF);
  final camera = CameraService.instance.firstCamera;

  void _incrementCounter() {
    setState(() {
      _counter++;
    });
  }

  @override
  void initState() {
    super.initState();
    _noiseMeter = NoiseMeter(onError);
  }

  @override
  void dispose() {
    _noiseSubscription?.cancel();
    super.dispose();
  }

  Future<void> _checkFlashState() async {
    bool isFlashActive = await Torch.instance.isTorched;
    setState(() {
      print('Coucou $isFlashActive');
      _isFlashActive = isFlashActive;
    });
  }

  Future<void> _makeVibration() async {
    bool? isVibration = await Vibration.hasCustomVibrationsSupport();
    setState(() {
      Vibration.vibrate(duration: 1000);
    });
  }

  void onData(NoiseReading noiseReading) {
    this.setState(() {
      _latestReading = noiseReading;
      if (!this._isRecording) this._isRecording = true;
    });
  }

  void onError(Object error) {
    print(error.toString());
    _isRecording = false;
  }

  void start() {
    try {
      print('quoi');
      _noiseSubscription = _noiseMeter.noise.listen(onData);
      this.setState(() {
        this._isRecording = true;
      });
    } catch (err) {
      print(err);
    }
  }

  void stop() {
    try {
      _noiseSubscription?.cancel();
      this.setState(() {
        this._isRecording = false;
      });
    } catch (err) {
      print(err);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            'Flash is ${_isFlashActive ? 'active' : 'inactive'}',
            style: const TextStyle(fontSize: 24, color: Colors.white),
          ),
          Text(
            'Vous avez cliqué $_counter fois',
            style: const TextStyle(fontSize: 20, color: Colors.white),
          ),
          const SizedBox(height: 20),
          Row(mainAxisAlignment: MainAxisAlignment.center, children: [
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: _checkFlashState,
              child: const Text('Check flash'),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: _incrementCounter,
              child: const Text('Cliquer'),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: _makeVibration,
              child: const Text('Vibration'),
            ),
          ]),
          const SizedBox(height: 20),
          ElevatedButton(
            onPressed: () async {
              Avataaar a = await Navigator.push(
                  context,
                  MaterialPageRoute(
                      builder: (ctx) => const UserProfilAvatar()));
            },
            child: const Text('Avatar'),
          ),
          const SizedBox(height: 20),
          Row(mainAxisAlignment: MainAxisAlignment.center, children: [
            ElevatedButton(
              onPressed: () async {
                Avataaar a = await Navigator.push(context,
                    MaterialPageRoute(builder: (ctx) => const QuestionPage()));
              },
              child: const Text('Template Question'),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () async {
                Avataaar a = await Navigator.push(context,
                    MaterialPageRoute(builder: (ctx) => const Question1Page()));
              },
              child: const Text('Template Question1'),
            ),
          ]),
          Row(mainAxisAlignment: MainAxisAlignment.center, children: [
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () async {
                Avataaar a = await Navigator.push(context,
                    MaterialPageRoute(builder: (ctx) => const Question2Page()));
              },
              child: const Text('Template Question2'),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () async {
                Avataaar a = await Navigator.push(context,
                    MaterialPageRoute(builder: (ctx) => const Question3Page()));
              },
              child: const Text('Template Question3'),
            ),

          ]),
          Row(mainAxisAlignment: MainAxisAlignment.center, children: [
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () async {
                Avataaar a = await Navigator.push(context,
                    MaterialPageRoute(builder: (ctx) => const Question4Page()));
              },
              child: const Text('Template Question4'),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () async {
                Avataaar a = await Navigator.push(context,
                    MaterialPageRoute(builder: (ctx) => const Question5Page()));
              },
              child: const Text('Template Question5'),
            ),

          ]),

          FloatingActionButton(
              backgroundColor: _isRecording ? Colors.red : Colors.green,
              onPressed: _isRecording ? stop : start,
              child: _isRecording
                  ? const Icon(Icons.stop)
                  : const Icon(Icons.mic)),
          Container(
            margin: const EdgeInsets.only(top: 20),
            child: Text(
              'Noise: ${_latestReading.maxDecibel} dB',
            ),
          ),
          if (face != null)
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Container(
                  width: 50,
                  height: 50,
                  decoration: BoxDecoration(
                    color: face,
                    borderRadius: BorderRadius.circular(10.0),
                  ),
                ),
                Text(
                  'Face color: ${face?.value.toRadixString(16)}',
                  style: const TextStyle(fontSize: 24, color: Colors.white),
                ),
              ],
            ),
          const SizedBox(height: 20),
          ElevatedButton(
            onPressed: () async {
              Uint8List? imageData = await Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (ctx) => TakePictureScreen(
                    camera: camera,
                  ),
                ),
              );
              if (imageData == null) {
                return;
              }
              setCustomColor(imageData);
            },
            child: const Text('Caméra'),
          ),
          const SizedBox(height: 20),
          ElevatedButton(
            onPressed: () async {
              Avataaar a = await Navigator.push(
                  context,
                  MaterialPageRoute(
                      builder: (ctx) => const WebsocketTestPage()));
            },
            child: const Text('Websocket'),
          ),
        ],
      ),
    );
  }

  void setCustomColor(Uint8List imageData) async {
    PaletteGenerator paletteGenerator =
        await PaletteGenerator.fromImageProvider(
      MemoryImage(imageData),
    );
    setState(() {
      face = paletteGenerator.dominantColor?.color;
    });
  }
}
