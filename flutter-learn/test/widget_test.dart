import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_learn/main.dart';

void main() {
  testWidgets('App renders correctly', (WidgetTester tester) async {
    await tester.pumpWidget(const FlutterLearnApp());
    expect(find.text('Flutter 学习'), findsOneWidget);
  });
}
