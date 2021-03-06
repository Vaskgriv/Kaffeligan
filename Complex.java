
public class Complex{
    double re,im;
    public Complex(){
        this(0,0);
    }
    public Complex(double re,double im){
        this.re=re;
        this.im=im;
    }
    public static Complex polar(double r,double ang){
        return new Complex(r*Math.cos(ang),r*Math.sin(ang));
    }
    public Complex add(Complex z){
        return new Complex(re+z.re,im+z.im);
    }
    public Complex scale(double c){
        return new Complex(re*c,im*c);
    }
    public Complex phaseShift(double ang){
        return new Complex(Math.cos(ang)*re,Math.sin(ang)*im);
    }
    public Complex subtract(Complex z){
        return this.add(z.scale(-1));
    }
    public double abs(){
        return Math.sqrt(re*re+im*im);
    }
    public double arg(){// returns 0 for 0+0i
        if(re==0){
            if(im>=0){
                return 0;
            }
            else{
                return Math.PI/2;
            }
        }
        double ang=Math.atan(im/re);
        return re<0?ang+Math.PI:ang;
    }
    public Complex multiply(Complex z){
        return polar(abs()*z.abs(),arg()+z.arg());
    }
    public Complex invert(){
        return polar(1/abs(),-arg());
    }
    public Complex divide(Complex z){
        return multiply(z.invert());
    }
    public Complex euler(){// exp(this.euler())==this
        return new Complex(Math.log(abs()),arg());
    }
    public Complex power(Complex z){
        Complex exponent=euler().multiply(z);
        return polar(Math.pow(Math.E,exponent.re),exponent.im);
    }
    public static Complex[] dft(double[] x){// not fft, square time
        Complex[] X=new Complex[x.length];// the transform of x is X by mathematical convention
        for(int i=0;i<X.length;i++){
            double k=2*i*Math.PI/x.length;
            X[i]=new Complex();
            for(int j=0;j<x.length;j++){
                X[i]=X[i].add(polar(x[j],k*j));
            }
        }
        return X;
    }
    public static Complex[] dft(int[] x){
        double[] y=new double[x.length];
        for(int i=0;i<y.length;i++){
            y[i]=(double)x[i];
        }
        return dft(y);
    }
    public static Complex[] fft(double[] x){
        if(x.length==1){
            return new Complex[]{new Complex(x[0],0)};// base case
        }
        x=fftZeroPad(x);
        double[] even=new double[x.length/2];
        double[] odd=new double[even.length];
        for(int i=0;i<even.length;i++){// split the input into even and odd indeces
            even[i]=x[2*i];
            odd[i]=x[2*i+1];
        }
        Complex[] evenX=fft(even);
        Complex[] oddX=fft(odd);
        for(int i=0;i<odd.length;i++){
            oddX[i]=oddX[i].phaseShift(2*Math.PI*i/x.length);
        }
        Complex[] X=new Complex[x.length];
        for(int i=0;i<even.length;i++){
            X[i]=evenX[i].add(oddX[i]);
            X[i+even.length]=evenX[i].subtract(oddX[i]);
        }
        return X;
    }
    public static Complex[] fft(int[] x){
        double[] y=new double[x.length];
        for(int i=0;i<y.length;i++){
            y[i]=(double)x[i];
        }
        return fft(y);
    }
    public static double[] fftZeroPad(double[] x){// returns x padded to a length that's a power of 2
        int exp=0;// first round the length of x up to a round power of 2
        int temp=x.length;
        int bits=0;
        while((temp>>>=1)>0){// bit-shift the length of x to the right
            exp++;// count the highest power of two
            bits+=temp & 1;// count the number of bits
        }
        if(bits>1){// if there's more than one bit, round up
            exp++;
        }
        else{// already an even power of 2, no padding needed
            return x;
        }
        int length=1;
        for(int i=0;i<exp;i++){// length=2^exp
            length*=2;
        }
        double[] y=new double[length];// create the new array
        for(int i=0;i<x.length;i++){// put in x
            y[i]=x[i];
        }
        for(int i=x.length;i<y.length;i++){// pad with zeros
            y[i]=0;
        }
        return y;
    }
}