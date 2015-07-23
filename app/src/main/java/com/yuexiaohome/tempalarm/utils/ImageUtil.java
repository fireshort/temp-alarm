package com.yuexiaohome.tempalarm.utils;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.opengl.GLES10;
//import android.support.v8.renderscript.Allocation;
//import android.support.v8.renderscript.Element;
//import android.support.v8.renderscript.RenderScript;
//import android.support.v8.renderscript.ScriptIntrinsicBlur;

/**
 * Created by Administrator on 2014/9/18.
 */
public final class ImageUtil
{

    static int MAX_TEXTURE_SIZE=2000;

    static
    {
        int[] params=new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE,params,0);
        MAX_TEXTURE_SIZE=params[0];
    }

    public static Bitmap getRoundedBitmap(Bitmap bm,float radius)
    {
        Bitmap output=Bitmap.createBitmap(bm.getWidth(),
                bm.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(output);

        final int color=0xff424242;
        final Paint paint=new Paint();
        final Rect rect=new Rect(0,0,bm.getWidth(),bm.getHeight());
        final RectF rectF=new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0,0,0,0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF,radius,radius,paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bm,rect,rect,paint);
        bm.recycle();
        return output;
    }

//    public static Bitmap rsBlur(Context context, Bitmap bitmap, float radius) {
//        bitmap = bitmap.copy(bitmap.getConfig(), true);
//        final RenderScript rs = RenderScript.create(context);
//        final Allocation input =
//                Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE,
//                        Allocation.USAGE_SCRIPT);
//        final Allocation output = Allocation.createTyped(rs, input.getType());
//        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
//
//        script.setRadius(radius);
//        script.setInput(input);
//        script.forEach(output);
//        output.copyTo(bitmap);
//
//        // clean up renderscript resources
//        rs.destroy();
//        input.destroy();
//        output.destroy();
//        script.destroy();
//
//        return bitmap;
//    }

    /**
     * Stack Blur v1.0 from
     * http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
     * <p/>
     * Java Author: Mario Klingemann <mario at quasimondo.com>
     * http://incubator.quasimondo.com
     * created Feburary 29, 2004
     * Android port : Yahel Bouaziz <yahel at kayenko.com>
     * http://www.kayenko.com
     * ported april 5th, 2012
     * <p/>
     * This is a compromise between Gaussian Blur and Box blur
     * It creates much better looking blurs than Box Blur, but is
     * 7x faster than my Gaussian Blur implementation.
     * <p/>
     * I called it Stack Blur because this describes best how this
     * filter works internally: it creates a kind of moving stack
     * of colors whilst scanning through the image. Thereby it
     * just has to add one new block of color to the right side
     * of the stack and remove the leftmost color. The remaining
     * colors on the topmost layer of the stack are either added on
     * or reduced by one, depending on if they are on the right or
     * on the left side of the stack.
     * <p/>
     * If you are using this algorithm in your code please add
     * the following line:
     * <p/>
     * Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>
     */
    public static Bitmap stackBlur(Bitmap sentBitmap,int radius)
    {

        Bitmap bitmap=sentBitmap.copy(sentBitmap.getConfig(),true);

        if(radius<1)
        {
            return (null);
        }

        int w=bitmap.getWidth();
        int h=bitmap.getHeight();

        int[] pix=new int[w*h];
        //L.e("pix,"+w+" "+h+" "+pix.length);
        bitmap.getPixels(pix,0,w,0,0,w,h);

        int wm=w-1;
        int hm=h-1;
        int wh=w*h;
        int div=radius+radius+1;

        int r[]=new int[wh];
        int g[]=new int[wh];
        int b[]=new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[]=new int[Math.max(w,h)];

        int divsum=(div+1)>>1;
        divsum*=divsum;
        int dv[]=new int[256*divsum];
        for(i=0; i<256*divsum; i++)
        {
            dv[i]=(i/divsum);
        }

        yw=yi=0;

        int[][] stack=new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1=radius+1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for(y=0; y<h; y++)
        {
            rinsum=ginsum=binsum=routsum=goutsum=boutsum=rsum=gsum=bsum=0;
            for(i=-radius; i<=radius; i++)
            {
                p=pix[yi+Math.min(wm,Math.max(i,0))];
                sir=stack[i+radius];
                sir[0]=(p&0xff0000)>>16;
                sir[1]=(p&0x00ff00)>>8;
                sir[2]=(p&0x0000ff);
                rbs=r1-Math.abs(i);
                rsum+=sir[0]*rbs;
                gsum+=sir[1]*rbs;
                bsum+=sir[2]*rbs;
                if(i>0)
                {
                    rinsum+=sir[0];
                    ginsum+=sir[1];
                    binsum+=sir[2];
                }else
                {
                    routsum+=sir[0];
                    goutsum+=sir[1];
                    boutsum+=sir[2];
                }
            }
            stackpointer=radius;

            for(x=0; x<w; x++)
            {

                r[yi]=dv[rsum];
                g[yi]=dv[gsum];
                b[yi]=dv[bsum];

                rsum-=routsum;
                gsum-=goutsum;
                bsum-=boutsum;

                stackstart=stackpointer-radius+div;
                sir=stack[stackstart%div];

                routsum-=sir[0];
                goutsum-=sir[1];
                boutsum-=sir[2];

                if(y==0)
                {
                    vmin[x]=Math.min(x+radius+1,wm);
                }
                p=pix[yw+vmin[x]];

                sir[0]=(p&0xff0000)>>16;
                sir[1]=(p&0x00ff00)>>8;
                sir[2]=(p&0x0000ff);

                rinsum+=sir[0];
                ginsum+=sir[1];
                binsum+=sir[2];

                rsum+=rinsum;
                gsum+=ginsum;
                bsum+=binsum;

                stackpointer=(stackpointer+1)%div;
                sir=stack[(stackpointer)%div];

                routsum+=sir[0];
                goutsum+=sir[1];
                boutsum+=sir[2];

                rinsum-=sir[0];
                ginsum-=sir[1];
                binsum-=sir[2];

                yi++;
            }
            yw+=w;
        }
        for(x=0; x<w; x++)
        {
            rinsum=ginsum=binsum=routsum=goutsum=boutsum=rsum=gsum=bsum=0;
            yp=-radius*w;
            for(i=-radius; i<=radius; i++)
            {
                yi=Math.max(0,yp)+x;

                sir=stack[i+radius];

                sir[0]=r[yi];
                sir[1]=g[yi];
                sir[2]=b[yi];

                rbs=r1-Math.abs(i);

                rsum+=r[yi]*rbs;
                gsum+=g[yi]*rbs;
                bsum+=b[yi]*rbs;

                if(i>0)
                {
                    rinsum+=sir[0];
                    ginsum+=sir[1];
                    binsum+=sir[2];
                }else
                {
                    routsum+=sir[0];
                    goutsum+=sir[1];
                    boutsum+=sir[2];
                }

                if(i<hm)
                {
                    yp+=w;
                }
            }
            yi=x;
            stackpointer=radius;
            for(y=0; y<h; y++)
            {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi]=(0xff000000&pix[yi])|(dv[rsum]<<16)|(dv[gsum]<<8)|dv[bsum];

                rsum-=routsum;
                gsum-=goutsum;
                bsum-=boutsum;

                stackstart=stackpointer-radius+div;
                sir=stack[stackstart%div];

                routsum-=sir[0];
                goutsum-=sir[1];
                boutsum-=sir[2];

                if(x==0)
                {
                    vmin[y]=Math.min(y+r1,hm)*w;
                }
                p=x+vmin[y];

                sir[0]=r[p];
                sir[1]=g[p];
                sir[2]=b[p];

                rinsum+=sir[0];
                ginsum+=sir[1];
                binsum+=sir[2];

                rsum+=rinsum;
                gsum+=ginsum;
                bsum+=binsum;

                stackpointer=(stackpointer+1)%div;
                sir=stack[stackpointer];

                routsum+=sir[0];
                goutsum+=sir[1];
                boutsum+=sir[2];

                rinsum-=sir[0];
                ginsum-=sir[1];
                binsum-=sir[2];

                yi+=w;
            }
        }

        bitmap.setPixels(pix,0,w,0,0,w,h);

        return bitmap;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

}


