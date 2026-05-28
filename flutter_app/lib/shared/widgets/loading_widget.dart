import 'package:flutter/material.dart';

class LoadingWidget extends StatelessWidget {
  final bool fullscreen;

  const LoadingWidget({super.key, this.fullscreen = false});

  @override
  Widget build(BuildContext context) {
    if (fullscreen) {
      return Container(
        color: Colors.black26,
        alignment: Alignment.center,
        child: const CircularProgressIndicator(),
      );
    }
    return const Center(child: CircularProgressIndicator());
  }
}
