package yousui115.unko;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Unko.MODID, version = Unko.VERSION)
public class Unko
{
    public static final String MODID = "unko";
    public static final String VERSION = "M190_F1887_v1";

    public static Item item_unko;
    public static String strItemUnko = "item_unko";

    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        //■アイテムのインスタンス生成
        item_unko = new ItemUnko(1, 0.1f, false)
                        .setCreativeTab(CreativeTabs.tabFood)   /*クリエイティブのタブ*/
                        .setUnlocalizedName(strItemUnko)        /*システム名の登録*/
                        .setMaxStackSize(64);                   /*スタックできる量。デフォルト64*/

        ResourceLocation rl = new ResourceLocation(MODID, strItemUnko);

        //■アイテムの登録。
        GameRegistry.register(item_unko, rl);

        if (event.getSide().isClient()) {
            ModelLoader.setCustomModelResourceLocation(item_unko, 0, new ModelResourceLocation(rl, "inventory"));
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        //■イベントの追加
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * ■Unko Attack
     * @param event
     */
    @SubscribeEvent
    public void attackOnUnko(AttackEntityEvent event)
    {
        ItemStack stack = event.getEntityPlayer().getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
        if (stack != null & stack.getItem() instanceof ItemUnko)
        {
            EntityDamageSource source = new EntityDamageSource("unko.dead.end", event.getEntityPlayer());
            source.setDamageIsAbsolute();
            event.getTarget().attackEntityFrom(source, 2);
        }
    }

    /**
     * ■Unko Damage
     * @param event
     */
    @SubscribeEvent
    public void damageOfUnko(LivingAttackEvent event)
    {
        if (event.getSource().damageType.equals("unko.dead.end"))
        {
            event.getEntityLiving().setHealth(0.01f);
        }
    }

    public static final TextFormatting[] color = {TextFormatting.DARK_RED,
                                                    TextFormatting.RED,
                                                    TextFormatting.YELLOW,
                                                    TextFormatting.GREEN,
                                                    TextFormatting.AQUA,
                                                    TextFormatting.BLUE,
                                                    TextFormatting.LIGHT_PURPLE
                                                 };
    public static int nTick = 0;
    public static final int nInterval = 10;
    public static final int nTickMax = nInterval * color.length;

    /**
     * ■Rainbow Unko (改名などさせない)
     * @param event
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void nameOfUnko(ItemTooltipEvent event)
    {
        if (event.getItemStack().getItem() instanceof ItemUnko)
        {
            int nStartIndex = nTick / nInterval;
            boolean isBreak = false;

            //■U☆N☆K☆O
            String strName = item_unko.getItemStackDisplayName(event.getItemStack());

            //■レインボー！
            String strColorN = "";
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

            event.getToolTip().set(0, strColorN.trim());

            if (++nTick >= nTickMax) { nTick = 0; }
        }
    }
}
