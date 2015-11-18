package yousui115.unko.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.unko.item.ItemUnko;

public class EventHooks
{
    public static EnumChatFormatting[] color = {EnumChatFormatting.DARK_RED,
                                                EnumChatFormatting.RED,
                                                EnumChatFormatting.YELLOW,
                                                EnumChatFormatting.GREEN,
                                                EnumChatFormatting.AQUA,
                                                EnumChatFormatting.BLUE,
                                                EnumChatFormatting.LIGHT_PURPLE
                                               };

    public static int nTick = 0;
    public static final int nInterval = 10;
    public static final int nTickMax = nInterval * color.length;

    @SubscribeEvent
    public void onPlayerTossEvent(ItemTooltipEvent event)
    {
        if (event.itemStack.getItem() instanceof ItemUnko)
        {
            int nStartIndex = nTick / nInterval;
            boolean isBreak = false;
            boolean isRename = false;
            //■0番目はきっとアイテム名！
            String strName = event.toolTip.get(0);
            if (strName.substring(0,2).contentEquals(EnumChatFormatting.ITALIC.toString()))
            {
                //■金床用
                strName = event.toolTip.get(0).substring(2);
                isRename = true;
            }
            String strColorN = "";
            //■レインボー！
            int i = 0;
            for (; i < color.length && i < strName.length(); i++)
            {
                int nNo = (nStartIndex + i) % color.length;
                if (strName.substring(i, i+1).contains("\u00a7")) { isBreak = true; break; }
                strColorN += color[nNo].toString() + strName.charAt(i);
            }

            //■文字列が残っているので連結。
            if (isBreak || i < strName.length())
            {
                strColorN += strName.substring(i);
            }
            //■改名してるので、イタリックに。
            if (isRename)
            {
                //TODO:何故かイタリックが反映されない
                strColorN = EnumChatFormatting.ITALIC.toString() + strColorN;
            }
            event.toolTip.set(0, strColorN.trim());

            if (++nTick >= nTickMax) { nTick = 0; }
        }
    }

    @SubscribeEvent
    public void onPlayerAttackEntity(AttackEntityEvent event)
    {
        if (!(event.target instanceof EntityLivingBase)) { return; }
        EntityLivingBase living = (EntityLivingBase)event.target;
        if (event.entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemUnko)
        {
            //■UNKOで殴られる = 死
            living.setHealth(0);

        }
    }
}
