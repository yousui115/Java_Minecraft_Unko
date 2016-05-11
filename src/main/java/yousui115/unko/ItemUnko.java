package yousui115.unko;

import java.util.List;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import com.google.common.base.Predicate;

public class ItemUnko extends ItemFood
{
    public ItemUnko(int amount, float saturation, boolean isWolfFood)
    {
        super(amount, saturation, isWolfFood);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player)
    {
        player.addPotionEffect(new PotionEffect(MobEffects.poison, 1200, 3));
        player.addPotionEffect(new PotionEffect(MobEffects.hunger, 300, 2));
        player.addPotionEffect(new PotionEffect(MobEffects.confusion, 300, 1));
        super.onFoodEaten(stack, worldIn, player);
    }

    /**
     * ■Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        //TODO 毎Tickやらなくてもいいんじゃないかな

        //■カレントアイテムでなければ処理を返す
        if (!isSelected) { return; }
        if (worldIn.isRemote) { return; }

        //■範囲内のEntityをかき集める。
        final double fExpand = 15;
        List<Entity> list = worldIn.getEntitiesWithinAABBExcludingEntity(entityIn, entityIn.getEntityBoundingBox().expand(fExpand, fExpand, fExpand));
        for (Entity entity : list)
        {
            //■エンダードラゴンは虚勢を張る
            if (entity instanceof EntityDragon) { continue; }

            //■生物である
            if (entity instanceof EntityLiving)
            {
                EntityLiving living = (EntityLiving)entity;

                //■クリーパー以外は戦意喪失
                //  (この条件でクリーパーの「死なばもろとも！ -> やっぱ無理！ -> ドゥーン！」が起こる)
                if (!(entity instanceof EntityCreeper))
                {
                    living.setAttackTarget(null);
                }

                //■調教済みかどうか調べる
                boolean isEscape = false;
                Set<EntityAITasks.EntityAITaskEntry> list_entry = living.tasks.taskEntries;
                for (EntityAITasks.EntityAITaskEntry entry : list_entry)
                {
                    //■調教済み
                    if (entry.action instanceof EntityAIAvoidUnko) { isEscape = true; break;}
                }

                if (!isEscape)
                {
                    //■調教
                    living.tasks.addTask(1, this.createAIAvoidUnko(living));
                    living.targetTasks.addTask(1, this.createAIAvoidUnko(living));
                }
            }
        }
    }

    /**
     * ■調教内容
     */
    public EntityAIBase createAIAvoidUnko(EntityLiving livingIn)
    {
        return new EntityAIAvoidUnko(livingIn, EntityLivingBase.class, new Predicate<EntityLivingBase>()
        {
            public boolean apply(EntityLivingBase livingIn)
            {
                if (livingIn == null) { return false; }

                ItemStack main = livingIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
                ItemStack off  = livingIn.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);

                //■手にうんこ持ってる
                return ((main != null && main.getItem() instanceof ItemUnko) ||
                        (off  != null && off.getItem()  instanceof ItemUnko) );
            }
        }, 15.0F, 1.0D, 3.0D);
    }
}