package yousui115.unko.item;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.world.World;
import yousui115.unko.entity.ai.EntityAIAvoidUnko;

import com.google.common.base.Predicate;

public class ItemUnko extends ItemFood
{
    public ItemUnko(int amount, float saturation, boolean isWolfFood)
    {
        super(amount, saturation, isWolfFood);
    }

    public String getPotionEffect(ItemStack stack)
    {
        return PotionHelper.pufferfishEffect;
    }

    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player)
    {
        player.addPotionEffect(new PotionEffect(Potion.poison.id, 1200, 3));
        player.addPotionEffect(new PotionEffect(Potion.hunger.id, 1200, 2));
        player.addPotionEffect(new PotionEffect(Potion.confusion.id, 1200, 3));
        super.onFoodEaten(stack, worldIn, player);
    }

    /**
     * ■Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        //■カレントアイテムでなければ処理を返す
        if (!isSelected) { return; }

        //■範囲内のEntityをかき集める。
        final double fExpand = 15;
        List<Entity> list = worldIn.getEntitiesWithinAABBExcludingEntity(entityIn, entityIn.getEntityBoundingBox().expand(fExpand, fExpand, fExpand));
        for (Entity entity : list)
        {
            //■生物以外は用なし
            //★ほとんどの生物はココ
            if (entity instanceof EntityLiving)
            {
                EntityLiving living = (EntityLiving)entity;

                //■調教済みかどうか調べる
                boolean isEscape = false;
                List<EntityAITasks.EntityAITaskEntry> list_entry = living.tasks.taskEntries;
                for (EntityAITasks.EntityAITaskEntry entry : list_entry)
                {
                    //■調教済み
                    if (entry.action instanceof EntityAIAvoidUnko) { isEscape = true; }
                }

                if (!isEscape)
                {
                    //■調教
                    living.tasks.addTask(0, this.createAIAvoidUnko(living));
                    living.targetTasks.addTask(0, this.createAIAvoidUnko(living));
                }
            }
        }
    }

    /**
     * ■EntityAIAvoidUnko の生成
     * UNKO持ってるPlayerを探す時に使用される。
     *  World.func_175674_a()内のgetEntitiesWithinAABBForEntity()に渡してる
     */
    public EntityAIBase createAIAvoidUnko(EntityLiving living)
    {
        return new EntityAIAvoidUnko(living, new Predicate()
        {
            public boolean func_179958_a(Entity entity)
            {
                if (!(entity instanceof EntityPlayer)) { return false; }

                //■プレイヤー かつ 手にうんこ持ってる
                EntityPlayer player = (EntityPlayer)entity;
                if (player.getCurrentEquippedItem() == null) { return false; }
                return player.getCurrentEquippedItem().getItem() instanceof ItemUnko;
            }
            public boolean apply(Object p_apply_1_)
            {
                return this.func_179958_a((Entity)p_apply_1_);
            }
        }, 15.0F, 1.0D, 3.0D);

    }
}
