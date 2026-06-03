import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:admin/shared/widgets/admin_data_table.dart';
import 'package:admin/shared/widgets/admin_dialog.dart';
import 'package:admin/shared/widgets/permission_widget.dart';

class GenTableListPage extends ConsumerStatefulWidget {
  const GenTableListPage({super.key});

  @override
  ConsumerState<GenTableListPage> createState() => _GenTableListPageState();
}

class _GenTableListPageState extends ConsumerState<GenTableListPage> {
  List<Map<String, dynamic>> _tables = [];
  bool _isLoading = true;
  int _currentPage = 1;
  int _total = 0;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _isLoading = true);
    await Future.delayed(const Duration(milliseconds: 500));
    setState(() {
      _tables = [
        {'id': 1, 'tableName': 'sys_user', 'tableComment': '用户信息表', 'className': 'SysUser', 'createTime': '2024-01-01'},
        {'id': 2, 'tableName': 'sys_role', 'tableComment': '角色信息表', 'className': 'SysRole', 'createTime': '2024-01-01'},
        {'id': 3, 'tableName': 'sys_menu', 'tableComment': '菜单权限表', 'className': 'SysMenu', 'createTime': '2024-01-01'},
      ];
      _total = 3;
      _isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    final columns = [
      AdminDataColumn<Map<String, dynamic>>(label: '表名称', getValue: (r) => r['tableName']),
      AdminDataColumn<Map<String, dynamic>>(label: '表描述', getValue: (r) => r['tableComment']),
      AdminDataColumn<Map<String, dynamic>>(label: '实体类名', getValue: (r) => r['className']),
      AdminDataColumn<Map<String, dynamic>>(label: '创建时间', getValue: (r) => r['createTime']),
      AdminDataColumn<Map<String, dynamic>>(
        label: '操作',
        getValue: (r) => '',
        cellBuilder: (context, record) => Row(
          children: [
            PermissionButton(
              permission: 'devtools:gen:preview',
              label: '预览',
              onPressed: () => _previewCode(record),
            ),
            const SizedBox(width: 8),
            PermissionButton(
              permission: 'devtools:gen:edit',
              label: '编辑',
              onPressed: () => _editTable(record),
            ),
            const SizedBox(width: 8),
            PermissionButton(
              permission: 'devtools:gen:download',
              label: '下载',
              onPressed: () => _downloadCode(record),
            ),
            const SizedBox(width: 8),
            PermissionButton(
              permission: 'devtools:gen:remove',
              label: '删除',
              isDestructive: true,
              onPressed: () => _deleteTable(record),
            ),
          ],
        ),
      ),
    ];

    return Scaffold(
      body: AdminDataTable<Map<String, dynamic>>(
        data: _tables,
        columns: columns,
        totalPages: (_total / 10).ceil(),
        currentPage: _currentPage,
        total: _total,
        onPageChanged: (page) => setState(() => _currentPage = page),
        onRefresh: _loadData,
        onAdd: _importTable,
        isLoading: _isLoading,
        toolbarActions: [
          PermissionButton(
            permission: 'devtools:gen:import',
            label: '导入表',
            icon: Icons.upload,
            onPressed: _importTable,
          ),
        ],
      ),
    );
  }

  void _importTable() {
    AdminDialog.showForm(
      context,
      title: '导入表',
      fields: [
        {'name': 'tableName', 'label': '表名称', 'type': 'text', 'required': true},
      ],
      onConfirm: (values) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('已导入表: ${values['tableName']}')),
        );
      },
    );
  }

  void _previewCode(Map<String, dynamic> table) {
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text('代码预览 - ${table['tableName']}'),
        content: SizedBox(
          width: 700,
          height: 500,
          child: DefaultTabController(
            length: 6,
            child: Column(
              children: [
                const TabBar(
                  isScrollable: true,
                  tabs: [
                    Tab(text: 'Entity'),
                    Tab(text: 'Mapper'),
                    Tab(text: 'Service'),
                    Tab(text: 'ServiceImpl'),
                    Tab(text: 'Controller'),
                    Tab(text: 'Mapper.xml'),
                  ],
                ),
                Expanded(
                  child: TabBarView(
                    children: List.generate(6, (index) {
                      return Container(
                        margin: const EdgeInsets.all(8),
                        padding: const EdgeInsets.all(12),
                        decoration: BoxDecoration(
                          color: Colors.grey.shade900,
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: SingleChildScrollView(
                          child: Text(
                            '// ${['Entity', 'Mapper', 'Service', 'ServiceImpl', 'Controller', 'Mapper.xml'][index]}.java\n// 代码预览区域\npackage com.admin.entity;\n\nimport lombok.Data;\n\n@Data\npublic class ${table['className']} {\n    // 字段...\n}',
                            style: const TextStyle(
                              fontFamily: 'monospace',
                              color: Colors.greenAccent,
                              fontSize: 13,
                            ),
                          ),
                        ),
                      );
                    }),
                  ),
                ),
              ],
            ),
          ),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('关闭')),
        ],
      ),
    );
  }

  void _editTable(Map<String, dynamic> table) {
    AdminDialog.showForm(
      context,
      title: '编辑生成配置 - ${table['tableName']}',
      initialValues: table,
      fields: [
        {'name': 'tableName', 'label': '表名称', 'type': 'text', 'required': true},
        {'name': 'tableComment', 'label': '表描述', 'type': 'text'},
        {'name': 'className', 'label': '类名称', 'type': 'text', 'required': true},
        {'name': 'packageName', 'label': '包路径', 'type': 'text', 'defaultValue': 'com.admin'},
        {'name': 'moduleName', 'label': '模块名', 'type': 'text'},
        {'name': 'businessName', 'label': '业务名', 'type': 'text'},
        {'name': 'functionName', 'label': '功能名', 'type': 'text'},
      ],
      onConfirm: (values) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('配置已更新')),
        );
      },
    );
  }

  void _downloadCode(Map<String, dynamic> table) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text('正在下载 ${table['tableName']} 代码...')),
    );
  }

  void _deleteTable(Map<String, dynamic> table) {
    AdminDialog.showConfirm(
      context,
      title: '确认删除',
      content: '确定要删除表 ${table['tableName']} 的生成配置吗？',
      onConfirm: () {
        setState(() => _tables.removeWhere((t) => t['id'] == table['id']));
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('已删除')),
        );
      },
    );
  }
}
