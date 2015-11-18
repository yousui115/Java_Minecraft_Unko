package yousui115.unko;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import yousui115.unko.event.EventHooks;
import yousui115.unko.item.ItemUnko;

@Mod(modid = Unko.MOD_ID, version = Unko.VERSION)
public class Unko
{
    public static final String MOD_ID = "unko";
    public static final String VERSION = "1.0";

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


        //■アイテムの登録。
        GameRegistry.registerItem(item_unko, strItemUnko);

        if (event.getSide().isClient()) {
            ModelLoader.setCustomModelResourceLocation(item_unko, 0, new ModelResourceLocation(MOD_ID + ":" + strItemUnko, "inventory"));
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        //■イベントの追加
        MinecraftForge.EVENT_BUS.register(new EventHooks());
    }
}
