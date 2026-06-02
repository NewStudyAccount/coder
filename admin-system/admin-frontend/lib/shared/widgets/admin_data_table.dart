import 'package:flutter/material.dart';

class AdminDataTable<T> extends StatelessWidget {
  final List<T> data;
  final List<AdminDataColumn<T>> columns;
  final int totalPages;
  final int currentPage;
  final int total;
  final ValueChanged<int> onPageChanged;
  final VoidCallback? onRefresh;
  final VoidCallback? onAdd;
  final List<Widget>? toolbarActions;
  final bool isLoading;

  const AdminDataTable({
    super.key,
    required this.data,
    required this.columns,
    required this.totalPages,
    required this.currentPage,
    required this.total,
    required this.onPageChanged,
    this.onRefresh,
    this.onAdd,
    this.toolbarActions,
    this.isLoading = false,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        if (onAdd != null || toolbarActions != null)
          Padding(
            padding: const EdgeInsets.only(bottom: 16),
            child: Row(
              children: [
                if (onAdd != null)
                  FilledButton.icon(
                    onPressed: onAdd,
                    icon: const Icon(Icons.add),
                    label: const Text('新增'),
                  ),
                const Spacer(),
                if (toolbarActions != null) ...toolbarActions!,
                if (onRefresh != null)
                  IconButton(
                    onPressed: onRefresh,
                    icon: const Icon(Icons.refresh),
                    tooltip: '刷新',
                  ),
              ],
            ),
          ),
        Expanded(
          child: isLoading
              ? const Center(child: CircularProgressIndicator())
              : data.isEmpty
                  ? Center(
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Icon(Icons.inbox_outlined, size: 64, color: theme.colorScheme.outline),
                          const SizedBox(height: 16),
                          Text('暂无数据', style: theme.textTheme.bodyLarge),
                        ],
                      ),
                    )
                  : SingleChildScrollView(
                      scrollDirection: Axis.horizontal,
                      child: DataTable(
                        headingRowColor: WidgetStateProperty.all(
                          theme.colorScheme.surfaceContainerHighest,
                        ),
                        columns: columns
                            .map((c) => DataColumn(label: Text(c.label)))
                            .toList(),
                        rows: data
                            .map((item) => DataRow(
                                  cells: columns
                                      .map((c) => DataCell(c.builder(item)))
                                      .toList(),
                                ))
                            .toList(),
                      ),
                    ),
        ),
        if (totalPages > 1)
          Padding(
            padding: const EdgeInsets.only(top: 16),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text('共 $total 条', style: theme.textTheme.bodySmall),
                Row(
                  children: [
                    IconButton(
                      onPressed: currentPage > 1
                          ? () => onPageChanged(currentPage - 1)
                          : null,
                      icon: const Icon(Icons.chevron_left),
                    ),
                    Text('$currentPage / $totalPages'),
                    IconButton(
                      onPressed: currentPage < totalPages
                          ? () => onPageChanged(currentPage + 1)
                          : null,
                      icon: const Icon(Icons.chevron_right),
                    ),
                  ],
                ),
              ],
            ),
          ),
      ],
    );
  }
}

class AdminDataColumn<T> {
  final String label;
  final Widget Function(T item) builder;

  const AdminDataColumn({
    required this.label,
    required this.builder,
  });
}
