import 'package:avataaars/avataaars.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class UserProfilAvatar extends StatefulWidget {
  final Avataaar? avatar;

  const UserProfilAvatar({Key? key, this.avatar}) : super(key: key);

  @override
  State<UserProfilAvatar> createState() => _UserProfilAvatarState();
}

class _UserProfilAvatarState extends State<UserProfilAvatar> {
  late Avataaar avatar;

  get _hairColors => const [
        Color(0xFF000000), // Noir
        Color(0xFF4B3D33), // Marron foncé
        Color(0xFFF9D56E), // Blond
        Color(0xFF967840), // Blond foncé
        Color(0xFF62200A), // Marron
        Color(0xFF8B2500), // Roux foncé
        Color(0xFFA52A2A), // Roux
        Color(0xFFFFA07A), // Roux clair
        Color(0xFFC0C0C0), // Gris
        Color(0xFF23B2B2), //bleu
        Color(0xFFE3267B), //rose
        Color(0xFFB426E3), //violet
        Color(0xFF8E26E3),
      ];

  @override
  void initState() {
    avatar = widget.avatar ??
        Avataaar.random().copyWith(
          style: const AvataaarStyle.circle(Colors.white10),
          hair: const AvataaarHair.longHairBob(),
          facialHair: const AvataaarFacialHair.blank(),
          accessory: const AvataaarAccessories.blank(),
        );
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Customise ton avatar'),
        actions: [
          IconButton(
            onPressed: () {
              Navigator.pop(context, avatar);
            },
            icon: const Icon(Icons.done),
          )
        ],
      ),
      body: DefaultTabController(
        length: 8,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            const SizedBox(
              height: 16,
            ),
            Center(
              child: SizedBox.square(
                dimension: 150,
                child: SvgPicture.string(
                  avatar.toSvg(),
                  width: 150,
                ),
              ),
            ),
            const SizedBox(
              height: 16,
            ),
            const TabBar(
              tabs: [
                Tab(
                  text: "Peau",
                ),
                Tab(
                  text: "Habits",
                ),
                Tab(
                  text: "Cheveux",
                ),
                Tab(
                  text: "Yeux",
                ),
                Tab(
                  text: "Bouche",
                ),
                Tab(
                  text: "Sourcils",
                ),
                Tab(
                  text: "Pilosité",
                ),
                Tab(
                  text: "Accessoires",
                ),
              ],
              isScrollable: true,
            ),
            Expanded(
              child: TabBarView(
                children: [
                  _SubPart(
                    items: AvataaarSkin.all,
                    onSelected: (int index) {
                      setState(() {
                        avatar = avatar.copyWith(skin: AvataaarSkin.all[index]);
                      });
                    },
                    generateSvg: (int index) {
                      return AvataaarSkin.all[index].toSvgPart();
                    },
                    current: avatar.eyes,
                  ),
                  _SubPartColor(
                    currentColor: avatar.clothes.color,
                    items: AvataaarClothes.all,
                    onSelected: (int index, Color color) {
                      setState(() {
                        avatar = avatar.copyWith(
                          clothes:
                              AvataaarClothes.all[index].copyWith(color: color),
                        );
                      });
                    },
                    generateSvg: (int index, Color color) {
                      return AvataaarClothes.all[index]
                          .copyWith(color: color)
                          .toSvgPart();
                    },
                    current: avatar.clothes,
                  ),
                  _SubPartColor(
                    currentColor: avatar.hair.color,
                    items: AvataaarHair.all,
                    onSelected: (int index, Color color) {
                      setState(() {
                        avatar = avatar.copyWith(
                          hair: AvataaarHair.all[index].copyWith(color: color),
                        );
                      });
                    },
                    generateSvg: (int index, Color color) {
                      return AvataaarHair.all[index]
                          .copyWith(color: color)
                          .toSvgPart();
                    },
                    current: avatar.hair,
                    colors: _hairColors,
                  ),
                  _SubPart(
                    items: AvataaarEyes.all,
                    onSelected: (int index) {
                      setState(() {
                        avatar = avatar.copyWith(eyes: AvataaarEyes.all[index]);
                      });
                    },
                    generateSvg: (int index) {
                      return AvataaarEyes.all[index].toSvgPart();
                    },
                    current: avatar.eyes,
                  ),
                  _SubPart(
                    items: AvataaarMouth.all,
                    onSelected: (int index) {
                      setState(() {
                        avatar =
                            avatar.copyWith(mouth: AvataaarMouth.all[index]);
                      });
                    },
                    generateSvg: (int index) {
                      return AvataaarMouth.all[index].toSvgPart();
                    },
                    current: avatar.mouth,
                  ),
                  _SubPart(
                    items: AvataaarEyebrow.all,
                    onSelected: (int index) {
                      setState(() {
                        avatar = avatar.copyWith(
                            eyebrow: AvataaarEyebrow.all[index]);
                      });
                    },
                    generateSvg: (int index) {
                      return AvataaarEyebrow.all[index].toSvgPart();
                    },
                    current: avatar.eyebrow,
                  ),
                  _SubPartColor(
                    currentColor: avatar.facialHair.color,
                    items: AvataaarFacialHair.all,
                    onSelected: (int index, Color color) {
                      setState(() {
                        avatar = avatar.copyWith(
                          facialHair: AvataaarFacialHair.all[index]
                              .copyWith(color: color),
                        );
                      });
                    },
                    generateSvg: (int index, Color color) {
                      return AvataaarFacialHair.all[index]
                          .copyWith(color: color)
                          .toSvgPart();
                    },
                    current: avatar.facialHair,
                    colors: _hairColors,
                  ),
                  _SubPart(
                    items: AvataaarAccessories.all,
                    onSelected: (int index) {
                      setState(() {
                        avatar = avatar.copyWith(
                            accessory: AvataaarAccessories.all[index]);
                      });
                    },
                    generateSvg: (int index) {
                      return AvataaarAccessories.all[index].toSvgPart();
                    },
                    current: avatar.accessory,
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _SubPart extends StatelessWidget {
  final List<AvataaarParts> items;
  final void Function(int index) onSelected;
  final AvataaarParts current;
  final String Function(int part) generateSvg;

  const _SubPart(
      {Key? key,
      required this.items,
      required this.onSelected,
      required this.current,
      required this.generateSvg})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GridView(
      gridDelegate:
          const SliverGridDelegateWithFixedCrossAxisCount(crossAxisCount: 3),
      children: [
        for (AvataaarParts item in items)
          GestureDetector(
            onTap: () {
              onSelected(items.indexOf(item));
            },
            child: builder(item),
          ),
      ],
    );
  }

  Widget builder(AvataaarParts value) {
    return Container(
      decoration: BoxDecoration(
        border: Border.all(
          color: current == value ? Colors.orange : Colors.transparent,
          width: 2,
        ),
        color: Colors.white10,
        borderRadius: BorderRadius.circular(10),
      ),
      margin: const EdgeInsets.all(5),
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: SvgPicture.string(
          generateSvg(value.toIndex()),
          width: 50,
        ),
      ),
    );
  }
}

class _SubPartColor extends StatelessWidget {
  final List<AvataaarParts> items;
  final void Function(int index, Color color) onSelected;
  final AvataaarParts current;
  final String Function(int part, Color color) generateSvg;
  final Color currentColor;
  final List<Color> colors;

  const _SubPartColor({
    Key? key,
    required this.items,
    required this.onSelected,
    required this.current,
    required this.generateSvg,
    required this.currentColor,
    this.colors = Colors.primaries,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        SizedBox(
          height: 50,
          child: ListView(
            scrollDirection: Axis.horizontal,
            children: [
              for (Color color in colors)
                GestureDetector(
                  onTap: () {
                    onSelected(current.toIndex(), color);
                  },
                  child: Container(
                    margin: const EdgeInsets.all(5),
                    decoration: BoxDecoration(
                      border: Border.all(
                        color: Colors.white,
                        width: 2,
                      ),
                      color: color,
                      borderRadius: BorderRadius.circular(10),
                    ),
                    width: 50,
                  ),
                ),
            ],
          ),
        ),
        Expanded(
          child: GridView(
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 3),
            children: [
              for (AvataaarParts item in items)
                GestureDetector(
                  onTap: () {
                    onSelected(item.toIndex(), currentColor);
                  },
                  child: builder(item),
                ),
            ],
          ),
        ),
      ],
    );
  }

  Widget builder(AvataaarParts value) {
    return Container(
      decoration: BoxDecoration(
        border: Border.all(
          color: current == value ? Colors.orange : Colors.transparent,
          width: 2,
        ),
        color: Colors.white10,
        borderRadius: BorderRadius.circular(10),
      ),
      margin: const EdgeInsets.all(5),
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: SvgPicture.string(
          generateSvg(value.toIndex(), currentColor),
          width: 50,
        ),
      ),
    );
  }
}
