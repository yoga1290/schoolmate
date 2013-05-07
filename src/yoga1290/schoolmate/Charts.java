package yoga1290.schoolmate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

public class Charts 
{
	public static int gcd(int a,int b)
	{
		if(b==0)
			return a;
		return gcd(b,a%b);
	}
	public static Bitmap getTimepiece(int width,int height,int ar[])//[0:8~9..11:7~8]=value
	{
		Bitmap bitmap=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		
		Canvas canvas=new Canvas(bitmap);
		
		int i,l=Math.min(ar.length, 12),angle=0,max=0,gcd=ar[0],lpad,tpad;
		for(i=0;i<ar.length;i++)
			gcd=gcd(ar[i],gcd);
		for(i=0;i<ar.length;i++)
			ar[i]=ar[i]/gcd;
		
		System.out.println("GCD="+gcd);
		for(i=0;i<ar.length;i++)
			max=Math.max(max,ar[i]);
		
		Paint paint=new Paint();
		paint.setColor(Color.WHITE);
		canvas.drawRect(0, 0, width,height, paint);
		int colors[]=new int[]{Color.BLUE,Color.CYAN,Color.GRAY,Color.GREEN};
		for(i=0;i<l;i++)
		{
			//radius
			lpad=ar[	(l-1-i	+4)%l	]*Math.min(width, height)/max;
			lpad>>=1;
		
			tpad=Math.min(width, height)-lpad;
//			System.out.println("LPAD="+lpad+",TPAD="+tpad);
			paint.setColor(colors[i%colors.length]);
			
			canvas.drawArc(new RectF(lpad,lpad, tpad, tpad), angle, 30, true, paint);
			
			angle+=30;
		}
		return bitmap;
	}
	public static Bitmap getClassTimetable(int width,int height,int ar[])
	{
		Bitmap bitmap=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas canvas=new Canvas(bitmap);
		int i,j,l=Math.min(ar.length, 6),angle=0,max=0,gcd=ar[0],lpad,tpad;
		for(i=0;i<ar.length;i++)
			max=Math.max(max,ar[i]);
		Paint paint=new Paint();
		paint.setColor(Color.WHITE);
		canvas.drawRect(0, 0, width,height, paint);
		int colors[]=new int[]{	Color.YELLOW, //Sunday
								Color.GRAY,  //Monday
								Color.RED,   //Tuesday
								Color.MAGENTA, //Wednesday
								Color.GREEN,   //Thursday
								Color.BLUE,    //Friday
								Color.WHITE};  //Saturday
		int dayR[]=new int[]{1,2,3,4,5,6,7};//{7,6,5,4,3,2,1};
		String days[]=new String[]{"S","M","T","W","T","F","Saturday"};		
		//Sunday w the biggest radius; Saturday w shortest 1
		for(j=0;j<7;j++)//j-th day of week
		{
			angle=30;
			for(i=0;i<l;i++)// i:0..6; periods
			{
				tpad=lpad=0;
//				//ar: array of flags[0:8~10..6:6~8], where each in form of 0bSMTWtFs
					if((ar[	(l-1-i	+4)%l ]&(1<<j))>0) //0 deg starts at i+4 anti-clockwise
					{
						lpad=Math.min(width, height)*dayR[j]/8;
						lpad>>=1;
					}

				if(lpad>0)
					tpad=Math.min(width, height)-lpad;
				paint.setColor(colors[j]);
				canvas.drawArc(new RectF(lpad,lpad, tpad, tpad), angle, 58, true, paint);
				angle+=60;
			}
		}
		for(i=0;i<7;i++)
		{
			paint.setColor(colors[i]);
			paint.setTextSize(20);
			canvas.drawText(days[i], i*20, height>>1, paint);
		}
		return bitmap;
	}
	
	public static Bitmap getGPAGraph(int width,int height,int ar[])//ar:GPA*100 across terms
	{
		Bitmap bitmap=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		
		Canvas canvas=new Canvas(bitmap);
		int gcd=ar[0],i,max=0,last=ar[ar.length-1];
		//get ratio by dividing by Greatest-Common Divisor
		for(i=0;i<ar.length;i++)
			gcd=gcd(ar[i],gcd);
		for(i=0;i<ar.length;i++)
			ar[i]=ar[i]/gcd;
		
		for(i=0;i<ar.length;i++)
			max=Math.max(max,ar[i]);
		int x,y,ox=0,oy=height;
		Paint paint=new Paint();
		paint.setColor(Color.WHITE);
		canvas.drawRect(0, 0, width,height, paint);
		paint.setColor(Color.BLACK);
		for(i=0;i<ar.length;i++)
		{
			x=i*width/ar.length;
			y=height-(ar[i]*height/max);
			
			paint.setColor(Color.BLACK);
			canvas.drawLine(ox, oy, x, y, paint);
			
			paint.setColor(Color.GRAY);
			canvas.drawLine(x, 0, x, height, paint);
			ox=x;
			oy=y;
		}
		paint.setColor(Color.GRAY);
		paint.setTextSize(32);
		canvas.drawText((last/100.0)+"", width>>1, height>>1, paint);
		return bitmap;
	}
}
