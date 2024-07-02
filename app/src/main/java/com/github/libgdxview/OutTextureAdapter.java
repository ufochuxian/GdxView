package com.github.libgdxview;

import android.util.Log;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.esotericsoftware.spine.*;
import com.esotericsoftware.spine.attachments.*;

public class OutTextureAdapter extends ApplicationAdapter {
    private SkeletonRenderer skeletonRenderer;
    private Batch batch;
    private TextureRegion hatTextureRegion;

    private TextureRegion paoTtureRegion;

    private Skeleton skeleton;
    private AnimationState animationState;

    private static final String TAG = "OutTextureAdapter";

    @Override
    public void create () {
        batch = new SpriteBatch();
        skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(true);



        // 加载Spine动画模型
        SkeletonJson json = new SkeletonJson(new TextureAtlas(Gdx.files.internal("goblins/goblins.atlas")));
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("goblins/goblins.json"));

        skeleton = new Skeleton(skeletonData);


        skeleton.setPosition(500, 0);
        skeleton.setToSetupPose();
        skeleton.getRootBone().setScale(3F);

        AnimationStateData animationStateData = new AnimationStateData(skeletonData);
        animationState = new AnimationState(animationStateData);

        Log.i(TAG,"load out texture");

         //加载外部贴图
        Texture newTexture = new Texture(Gdx.files.internal("hat.png"));
        newTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        hatTextureRegion = new TextureRegion(newTexture);

        Texture paoTexture = new Texture(Gdx.files.internal("大炮.png"));
        newTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        paoTtureRegion = new TextureRegion(paoTexture);
//
//        // 找到要替换的Attachment对象
//        Attachment attachment = skeleton.getAttachment("undies", "undies");
//        if (attachment instanceof RegionAttachment) {
//            // 将TextureRegion对象传递给Attachment对象，替换原有的贴图
//            ((RegionAttachment)attachment).setRegion(newTextureRegion);
//        }

    }

    @Override
    public void render () {

        Log.i(TAG,"render function has been called");
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        skeletonRenderer.draw(batch, skeleton);
        batch.end();

        animationState.update(Gdx.graphics.getDeltaTime());
        animationState.apply(skeleton);
        skeleton.updateWorldTransform();
    }

    @Override
    public void dispose () {
        batch.dispose();
        hatTextureRegion.getTexture().dispose();
        paoTtureRegion.getTexture().dispose();
    }

    public void updateskins() {
        //updateAnimation
        animationState.setEmptyAnimations(0);
        setAnimate("walk");

        skeleton.setSkin("custom"); // 1. 设置使用皮肤
        skeleton.setSlotsToSetupPose(); // 2. 使用装配姿势设置基础附件。
        animationState.apply(skeleton); // 3. 使用AnimationState设置当前运动中使用的附件。
        // 4. 设置手动更换的附件。
//        skeleton.setAttachment("undies","undies");

    }

    public void clearSlotAttachment() {
        Slot undies = skeleton.findSlot("undies");
        Attachment attachment = undies.getAttachment();
        if(attachment != null) {
            undies.setAttachment(null);
        }
    }

    public void setSlotAttachment(boolean turnlight) {

        TextureRegion textureRegion = turnlight ? hatTextureRegion : paoTtureRegion;
        // 找到要替换的Attachment对象
        Attachment attachment = skeleton.getAttachment("undies", "undies");
        if (attachment != null && attachment instanceof RegionAttachment) {
            // 将TextureRegion对象传递给Attachment对象，替换原有的贴图
            ((RegionAttachment)attachment).setRegion(textureRegion);
        }


    }

    public void changeSlotColor() {
        //更换slot的颜色
        Slot leftArmSlot = skeleton.findSlot("left arm");
        leftArmSlot.getColor().set(com.badlogic.gdx.graphics.Color.BLUE);

        Slot headSlot = skeleton.findSlot("head");
        headSlot.getColor().set(Color.MAGENTA);
    }



    public void setAnimate(String animate) {
        animationState.addAnimation(0, animate, true, 0);
    }
}
