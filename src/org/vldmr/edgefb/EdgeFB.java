/*
 * Copyright (C) 2011 vldmr
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

/*
 *  Class implementing access to entourage edge frame buffer.
 *  Based on "Send to Framebuffer" application by Sven Killig
 *  http://sven.killig.de/android/N1/2.2/usb_host
 */

package org.vldmr.edgefb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Color;

public class EdgeFB
{

    private int framebuffer, width, height, rotate, stride;

    public EdgeFB(int framebuffer) throws IOException
    {
        this.framebuffer=framebuffer;
        BufferedReader buf=new BufferedReader(new FileReader("/sys/class/graphics/fb"+framebuffer+"/rotate"));
        String s=buf.readLine();
        buf.close();
        rotate = Integer.parseInt(s);

        buf=new BufferedReader(new FileReader("/sys/class/graphics/fb"+framebuffer+"/stride"));
        s=buf.readLine();
        buf.close();
        stride = Integer.parseInt(s);

        buf=new BufferedReader(new FileReader("/sys/class/graphics/fb"+framebuffer+"/virtual_size"));
        String[] as=buf.readLine().split(",");
        buf.close();
        width=Integer.parseInt(as[0]);
        height=Integer.parseInt(as[1]);

        if (rotate == 90 || rotate == 270)
            if (width == 800)
                width = 600; // edgejr
            else
                width = 875; // edge

        if (rotate == 0 || rotate == 180)
            if (height == 800)
                height = 600; // edgejr
            else
                height = 875; // edge
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getRotate()
    {
        return rotate;
    }

    public Bitmap createBitmap()
    {
        return Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
    }

    public void transfer(Bitmap bitmap, boolean recycle) throws IOException
    {
        OutputStream os=new FileOutputStream("/dev/graphics/fb"+framebuffer);
        int WIDTH=bitmap.getWidth(), HEIGHT=bitmap.getHeight();
        int dim=WIDTH*HEIGHT;
        int[] pixels=new int[dim];
        bitmap.getPixels(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);

        if(recycle)
            bitmap.recycle();

        byte[] buffer=new byte[HEIGHT*stride];

        int co;

        int o = 0;

        for(int i=0; i<HEIGHT; i++)
            for (int j = 0; j < WIDTH; j++) {
                buffer[i*stride + j]=(byte)(Color.red(pixels[o++]) & 0xff );
            }

        os.write(buffer);
        os.close();
    }
}
