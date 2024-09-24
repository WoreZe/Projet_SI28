import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:palette_generator/palette_generator.dart';

import '../Services/camera_services.dart';
import 'camera_page.dart';

class Question3Page extends StatefulWidget {
  const Question3Page({super.key});

  @override
  State<Question3Page> createState() => _Question3PageState();
}

class _Question3PageState extends State<Question3Page> {
  final camera = CameraService.instance.firstCamera;
  Color? face = new Color(0xFFFFFFFF);
  late Future<bool> is_green;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title:
          Text("Nom de l'histoire", style: TextStyle(fontFamily: 'madimi')),
        ),
        body: Padding(
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
                      "Vous tenez dans vos mains la tablette, il est de temps de prendre des photos !",
                      style: TextStyle(
                        fontSize: 24,
                        color: Colors
                            .white, // Définit la couleur du texte en blanc
                      ),
                    ),
                  )
              ),
              ElevatedButton(
                style: ButtonStyle(
                  backgroundColor: MaterialStateProperty.all<Color>(Colors.orange), // Changer la couleur du bouton
                ),
                onPressed: () async {
                  Uint8List? imageData = await Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (ctx) =>
                          TakePictureScreen(
                            camera: camera,
                          ),
                    ),
                  );
                  if (imageData == null) {
                    return;
                  }
                  is_green = setCustomColor(imageData);
                  checkIfGreen();
                },
                  child: Icon(
                    Icons.camera,
                    color: Colors.white,
                  ),
              ),

            ],
          ),
        )
    );
  }

  Future<bool> setCustomColor(Uint8List imageData) async {
    PaletteGenerator paletteGenerator =
    await PaletteGenerator.fromImageProvider(
      MemoryImage(imageData),
    );
    face = paletteGenerator.dominantColor?.color;
    int colorInt = int.parse(face!.value.toRadixString(16), radix: 16);
    int red = (colorInt >> 16) & 0xFF;
    int green = (colorInt >> 8) & 0xFF;
    int blue = colorInt & 0xFF;

    // Check if green is the dominant color.
    return green > red && green > blue;
  }

  void checkIfGreen() {
    is_green.then((value) {
      print('Is the color green? $value');
    }).catchError((error) {
      print('An error occurred: $error');
    });
  }

}
