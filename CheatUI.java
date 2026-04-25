package cheatmod;

import mindustry.ui.dialogs.BaseDialog;
import mindustry.Vars;
import arc.scene.ui.layout.Table;
import arc.scene.ui.TextButton;
import arc.scene.ui.CheckBox;
import arc.scene.ui.SelectBox;
import arc.util.Http;
import arc.util.serialization.Json;
import java.io.*;
import java.util.*;

public class CheatUI {
    private static boolean godMode, instantBuild, infiniteResources, infinitePower, noDamage;
    private static Map<String, Map<String, Boolean>> profiles=new LinkedHashMap<>();
    private static SelectBox<String> selector=new SelectBox<>();
    private static Json json=new Json();

    public static void loadUI(){
        BaseDialog d=new BaseDialog("Cheats");
        Table c=new Table();

        c.add(toggle("God Mode",()->godMode,b->{godMode=b;Vars.player.unit().health=b?Float.MAX_VALUE:Vars.player.unit().type.health;})).row();
        c.add(toggle("Instant Build",()->instantBuild,b->{instantBuild=b;Vars.state.rules.infiniteResources=b;Vars.state.rules.infiniteAmmo=b;})).row();
        c.add(toggle("Recursos Infinitos",()->infiniteResources,b->{infiniteResources=b;Vars.state.rules.infiniteResources=b;})).row();
        c.add(toggle("Energía Infinita",()->infinitePower,b->{infinitePower=b;Vars.state.rules.infinitePower=b;})).row();
        c.add(toggle("No Damage",()->noDamage,b->{noDamage=b;Vars.state.rules.damageMultiplier=b?0f:1f;})).row();

        selector.setItems(profiles.keySet().toArray(new String[0]));
        c.add(selector).row();

        TextButton save=new TextButton("Guardar Perfil");
        save.clicked(()->{String n="Perfil_"+System.currentTimeMillis();Map<String,Boolean> p=new HashMap<>();
            p.put("godMode",godMode);p.put("instantBuild",instantBuild);p.put("infiniteResources",infiniteResources);
            p.put("infinitePower",infinitePower);p.put("noDamage",noDamage);profiles.put(n,p);
            selector.setItems(profiles.keySet().toArray(new String[0]));});
        c.add(save).row();

        TextButton load=new TextButton("Cargar Perfil");
        load.clicked(()->{String s=selector.getSelected();if(s!=null&&profiles.containsKey(s)){Map<String,Boolean> p=profiles.get(s);
            godMode=p.getOrDefault("godMode",false);instantBuild=p.getOrDefault("instantBuild",false);
            infiniteResources=p.getOrDefault("infiniteResources",false);infinitePower=p.getOrDefault("infinitePower",false);
            noDamage=p.getOrDefault("noDamage",false);}});
        c.add(load).row();

        TextButton exportB=new TextButton("Exportar JSON");
        exportB.clicked(()->{try(Writer w=new FileWriter("cheat_profiles.json")){json.toJson(profiles,w);}catch(Exception e){}});
        c.add(exportB).row();

        TextButton importB=new TextButton("Importar JSON");
        importB.clicked(()->{try(Reader r=new FileReader("cheat_profiles.json")){profiles=json.fromJson(LinkedHashMap.class,r);
            selector.setItems(profiles.keySet().toArray(new String[0]));}catch(Exception e){}});
        c.add(importB).row();

        TextButton upload=new TextButton("Subir al servidor");
        upload.clicked(()->{Http.post("https://tu-dominio.com/api/perfiles",json.toJson(profiles),res->{},err->{});});
        c.add(upload).row();

        TextButton download=new TextButton("Descargar comunidad");
        download.clicked(()->{Http.get("https://tu-dominio.com/api/perfiles",res->{profiles.putAll(json.fromJson(LinkedHashMap.class,res.getResultAsString()));
            selector.setItems(profiles.keySet().toArray(new String[0]));},err->{});});
        c.add(download).row();

        d.cont.add(c).row();d.addCloseButton();d.show();
    }

    private static CheckBox toggle(String label,Supplier<Boolean> g,Consumer<Boolean> s){
        CheckBox b=new CheckBox(label);b.setChecked(g.get());b.changed(v->s.accept(v));return b;}
    private interface Supplier<T>{T get();}private interface Consumer<T>{void accept(T t);}
}