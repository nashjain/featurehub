import 'package:flutter/material.dart';
import 'package:mrapi/api.dart';
import 'package:open_admin_app/widgets/features/custom_strategy_bloc.dart';
import 'package:open_admin_app/widgets/features/table-expanded-view/strategies/strategy_card_widget.dart';
import 'package:open_admin_app/widgets/features/table-expanded-view/string/edit_string_value_container.dart';

class StringStrategyCard extends StatelessWidget {
  final RolloutStrategy? rolloutStrategy;
  final CustomStrategyBloc strBloc;

  const StringStrategyCard(
      {Key? key, this.rolloutStrategy, required this.strBloc})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return StreamBuilder<bool>(
        stream: strBloc.fvBloc.environmentIsLocked(
            strBloc.environmentFeatureValue.environmentId!),
        builder: (ctx, snap) {
          if (snap.hasData) {
            final editable = strBloc.environmentFeatureValue.roles
                .contains(RoleType.CHANGE_VALUE);
            final unlocked = !snap.data!;
            return StrategyCardWidget(
              editable: editable,
              strBloc: strBloc,
              rolloutStrategy: rolloutStrategy,
              editableHolderWidget: EditStringValueContainer(
                canEdit: editable,
                unlocked: unlocked,
                rolloutStrategy: rolloutStrategy,
                strBloc: strBloc,
              ),
            );
          } else {
            return const SizedBox.shrink();
          }
        });
  }
}
