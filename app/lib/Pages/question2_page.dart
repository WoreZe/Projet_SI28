import 'dart:async';

import 'package:flutter/material.dart';
import 'package:noise_meter/noise_meter.dart';
import 'package:torchx/module/torch.dart';

class Question2Page extends StatefulWidget {
  const Question2Page({super.key});

  @override
  State<Question2Page> createState() => _Question2PageState();
}

class _Question2PageState extends State<Question2Page> {
  bool _isFlashActive = true;
  bool _isRecording = false;
  NoiseReading _latestReading = NoiseReading([0, 0]);
  StreamSubscription<NoiseReading>? _noiseSubscription;
  late NoiseMeter _noiseMeter;

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
      _isFlashActive = isFlashActive;
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
    return Scaffold(
      appBar: AppBar(
        title:
            Text("Nom de l'histoire", style: TextStyle(fontFamily: 'madimi')),
      ),
      body: _isFlashActive
          ? Padding(
              padding: EdgeInsets.all(16.0),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Container(
                      decoration: BoxDecoration(
                        color: Colors.orange,
                        borderRadius: BorderRadius.circular(
                            10.0), // Définit la couleur de fond en orange
                      ),
                      // Définit la largeur de la boîte contenant le texte
                      padding: EdgeInsets.all(10),
                      margin: EdgeInsets.only(bottom: 20.0),
                      // Ajoute un remplissage intérieur
                      child: Center(
                        child: Text(
                          "Savez vous vraiment communiquer avec un chat ?",
                          style: TextStyle(
                            fontSize: 24,
                            color: Colors
                                .white, // Définit la couleur du texte en blanc
                          ),
                        ),
                      )),
                  FloatingActionButton(
                    backgroundColor: _isRecording ? Colors.red : Colors.orange,
                    onPressed: _isRecording ? stop : start,
                    child: _isRecording ? Icon(Icons.stop) : Icon(Icons.mic),
                  ),
                ],
              ),
            )
          : Center(
              child: Container(
                  color: Colors.black,
                  padding: EdgeInsets.all(24.0),
                  // Définit la largeur de la boîte contenant le texte
                  child: Center(
                    child: Text(
                      "Il fait bien sombre ici",
                      style: TextStyle(
                        fontSize: 24,
                        color: Colors
                            .white, // Définit la couleur du texte en blanc
                      ),
                    ),
                  )),
            ),
    );
  }
}
