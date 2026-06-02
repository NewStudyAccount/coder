import 'package:flutter/material.dart';

class OperLogListPage extends StatelessWidget {
  const OperLogListPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(child: Text('操作日志', style: Theme.of(context).textTheme.headlineSmall));
  }
}
