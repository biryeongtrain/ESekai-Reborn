package net.qf.impl;

import net.minecraft.util.ActionResult;
import net.qf.api.ESekaiCreationSkill;
import net.qf.api.TriggerType;

public interface ESekaiSkillUser {
    boolean hasSkill();
    boolean canCastSkill(TriggerType type);
    void setSkill(ESekaiCreationSkill skill);
    boolean hasActiveSkill();
    ActionResult castActiveSkill();
    boolean hasPassiveSkill(TriggerType type);
    ActionResult castPassiveSkill(TriggerType type);
    void setCooldown(TriggerType type, int tick);
}
