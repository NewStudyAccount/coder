import 'package:flutter/material.dart';

class NoticeListPage extends StatelessWidget {
  const NoticeListPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(child: Text('通知公告', style: Theme.of(context).textTheme.headlineSmall));
  }
}
