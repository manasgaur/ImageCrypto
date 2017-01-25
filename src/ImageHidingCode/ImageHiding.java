package ImageHidingCode;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.io.*;

import javax.imageio.ImageIO;

import javax.swing.*;

public class ImageHiding extends JFrame implements ActionListener
{
 BufferedImage hostImage;
 BufferedImage secretImage;

 JPanel controlPanel;
 JPanel imagePanel;

 JTextField encodeBitsText;
 JButton encodeBitsPlus;
 JButton encodeBitsMinus;

 JTextField nBitsText;
 JButton nBitsPlus;
 JButton nBitsMinus;

 ImageCanvas hostCanvas;
 ImageCanvas secretCanvas;
 JTextField jtf=new JTextField("0",5);
 //layout.setConstraints(jtf, gbc);

 Steganography s;

 public BufferedImage getHostImage()
 {
  BufferedImage img = null;

  try
  {
   img = ImageIO.read(new File(" location of hostimage.jpg"));
  }
  catch (IOException ioe) { ioe.printStackTrace(); }

  return img;
 }
 static String getBits_data(byte b)
	{
	    String result = "";
	    for(int i = 0; i < 8; i++)
	        result += (b & (1 << i)) == 0 ? "0" : "1";
	    return result;
	}
 public String getSecretText()//BufferedImage getSecretImage()
 {
  // BufferedImage img = null;
  BufferedReader br= null;
  String line="";
  String content="";
  try
  {
   //img = ImageIO.read((new File("location of secret_image.jpg")));//"//Users//manasgaur//Documents//javaworkspace//Crypto//Text.jpg")));
	  br=new BufferedReader(new FileReader("location of Text : text.txt"));
	    
	    
		while((line=br.readLine())!=null)
		{
			byte []bst=line.getBytes();
			for(byte b: bst)
			{
				content+=getBits_data(b);
			}
		}
   //ImageIO.read();
  }
  catch (IOException ioe) { ioe.printStackTrace(); }

  return content;
 }

 public int getBits()
 {
  return Integer.parseInt(encodeBitsText.getText());
 }

 public void actionPerformed(ActionEvent event)
 {
  Object source = event.getSource();

  if (source == encodeBitsPlus)
  {
   int bits = this.getBits() + 1;

   if (bits > 8) { bits = 8; }

   encodeBitsText.setText(Integer.toString(bits));

   s = new Steganography(this.getHostImage());
   //s.encode(this.getSecretImage(), bits);
   s.getencodeData(this.getSecretText(),bits);
   hostCanvas.setImage(s.getImage());
   hostCanvas.repaint();

  
  // Steganography dummy=new Steganography();
   jtf.setText(String.valueOf(s.change()));
  /* s = new Steganography(this.getSecretText());
   s.getMaskedImage(bits);

   secretCanvas.setImage(s.getImage());
   secretCanvas.repaint();*/
  }
  else if (source == encodeBitsMinus)
  {
   int bits = this.getBits() - 1;

   if (bits < 0) { bits = 0; }

   encodeBitsText.setText(Integer.toString(bits));

   s = new Steganography(this.getHostImage());
   s.getencodeData(this.getSecretText(), bits);

   hostCanvas.setImage(s.getImage());
   hostCanvas.repaint();

   jtf.setText(String.valueOf(s.change()));
  /* s = new Steganography(this.getSecretImage());
   s.getMaskedImage(bits);
   
   secretCanvas.setImage(s.getImage());
   secretCanvas.repaint();*/
  }
 }

 public ImageHiding()
 {
  GridBagLayout layout = new GridBagLayout();
  GridBagConstraints gbc = new GridBagConstraints();
  this.setTitle("Image Hiding Demo");

  Container container = this.getContentPane();

  this.setLayout(layout);

  this.add(new JLabel("Bits to encode into host image:"));

  encodeBitsText = new JTextField("0", 5);
  encodeBitsText.setEditable(false);
  
  gbc.weightx = -1.0;
  layout.setConstraints(encodeBitsText, gbc);
  this.add(encodeBitsText);
  this.add(jtf);
  encodeBitsPlus = new JButton("+");
  encodeBitsPlus.addActionListener(this);

  encodeBitsMinus = new JButton("-");
  encodeBitsMinus.addActionListener(this);

  gbc.weightx = 1.0;
  layout.setConstraints(encodeBitsPlus, gbc);
  this.add(encodeBitsPlus);

  gbc.gridwidth = GridBagConstraints.REMAINDER;
  layout.setConstraints(encodeBitsMinus, gbc);
  this.add(encodeBitsMinus);

  GridBagLayout imageGridbag = new GridBagLayout();
  GridBagConstraints imageGBC = new GridBagConstraints();

  imagePanel = new JPanel();
  imagePanel.setLayout(imageGridbag);

  JLabel hostImageLabel = new JLabel("Host image:");
  JLabel secretImageLabel = new JLabel("Secret image:");
  imagePanel.add(hostImageLabel);

  imageGBC.gridwidth = GridBagConstraints.REMAINDER;
  imageGridbag.setConstraints(secretImageLabel, imageGBC);
  //imagePanel.add(secretImageLabel);
  
  hostCanvas = new ImageCanvas(this.getHostImage());  
  //secretCanvas = new ImageCanvas(this.getSecretImage());

  imagePanel.add(hostCanvas);
  //imagePanel.add(secretCanvas);

  gbc.gridwidth = GridBagConstraints.REMAINDER;
  layout.setConstraints(imagePanel, gbc);
  this.add(imagePanel);

  Steganography host = new Steganography(this.getHostImage());
 // host.encode(this.getSecretImage(), this.getBits());
  host.getencodeData(this.getSecretText(),this.getBits());
  hostCanvas.setImage(host.getImage());

  //Steganography secret = new Steganography(this.getSecretText());//this.getSecretImage());
  //secret.getMaskedImage(this.getBits());
  //secretCanvas.setImage(secret.getImage());
  
  this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  this.pack();

  this.setVisible(true);
 }

 public static void main(String[] args)
 {
  ImageHiding frame = new ImageHiding();
  frame.setVisible(true);
 }

 public class ImageCanvas extends JPanel
 { 
  Image img;

  public void paintComponent(Graphics g)
  {
   g.drawImage(img, 0, 0, this);
  }

  public void setImage(Image img)
  {
   this.img = img;
  }

  public ImageCanvas(Image img)
  {
   this.img = img;

   this.setPreferredSize(new Dimension(img.getWidth(this), img.getHeight(this)));
  }
 }
}

class Steganography
{
 BufferedImage image;
 static String []masktext;
 int newsize=0;
 public void getMaskedImage(int bits)
 {
  int[] imageRGB = image.getRGB(0, 0, image.getWidth(null), image.getHeight(null), null, 0, image.getWidth(null));

  int maskBits = (int)(Math.pow(2, bits)) - 1 << (8 - bits);
  int mask = (maskBits << 24) | (maskBits << 16) | (maskBits << 8) | maskBits;

  for (int i = 0; i < imageRGB.length; i++)
  {
   imageRGB[i] = imageRGB[i] & mask;
  }

  image.setRGB(0, 0, image.getWidth(null), image.getHeight(null), imageRGB, 0, image.getWidth(null));
 }
 
 public void getencodeData(String content, int bits) // this is the main function for the textual data
 // Morevover, this function takes the textual content in bit formats and embed it in the image
 // Now executing the code
 {
	 int maskBits = (int)(Math.pow(2, bits)) - 1 << (8 - bits);
	 
	 int sum=0;
	 //String newcontent="";
	  int mask = (maskBits << 24) | (maskBits << 16) | (maskBits << 8) | maskBits;
     String []maskedtext=content.split("");
     masktext=new String[content.length()];
	  for (int i = 0; i < content.length(); i++)
	  {
	  masktext[i] = String.valueOf((Integer.parseInt(maskedtext[i]) & mask));
	  //newcontent+=masktext[i];
	  }
	  //int data=Integer.parseInt(content);
	// encode the image with the text
	  int[] imageRGB = image.getRGB(0, 0, image.getWidth(null), image.getHeight(null), null, 0, image.getWidth(null));
	  int encodeByteMask = (int)(Math.pow(2, bits)) - 1 << (8 - bits);
	  int encodeMask = (encodeByteMask << 24) | (encodeByteMask << 16) | (encodeByteMask << 8) | encodeByteMask;

	  int decodeByteMask = ~(encodeByteMask >>> (8 - bits)) & 0xFF;
	  int hostMask = (decodeByteMask << 24) | (decodeByteMask << 16) | (decodeByteMask << 8) | decodeByteMask;

	  for(int i=0; i<imageRGB.length; i++)
	  {
		  int encodedata= (Integer.parseInt(maskedtext[i])&encodeMask)>>>(8-bits);
	      imageRGB[i] = (imageRGB[i] & hostMask) | (encodedata & ~hostMask);
	  }
	  image.setRGB(0, 0, image.getWidth(null), image.getHeight(null), imageRGB, 0, image.getWidth(null));
	  for (int i = 0; i < image.getHeight(); i++) {
	      for (int j = 0; j < image.getWidth(); j++) {
	        sum += image.getRGB(j, i);
	      }
	    }
	  try
	  {
	  ByteArrayOutputStream tmp=new ByteArrayOutputStream();
	  ImageIO.write(image, "png", tmp);
	  newsize=tmp.size(); // managing the bits for the image after data is embedded 
	  //System.out.println(tmp.size());
	  }
	  catch(IOException ioe)
	  {
	  ioe.printStackTrace();
	  }
	  //lettercount();
 }
 
 public int change()
 {
	 return newsize;
 }
 /*public static void lettercount()
 {
	// int[] numbers = new int[masktext.length];
	 int sum=0;
	 for(int i = 0;i < masktext.length;i++)
	 {
	    // Note that this is assuming valid input
	    // If you want to check then add a try/catch 
	    // and another index for the numbers if to continue adding the others
	    sum+= Integer.parseInt(masktext[i]);
	 }
	 System.out.println(sum);
	 //return sum;
 }*/

 public void encode(BufferedImage encodeImage, int encodeBits)
 {
  int[] encodeRGB = encodeImage.getRGB(0, 0, encodeImage.getWidth(null), encodeImage.getHeight(null), null, 0, encodeImage.getWidth(null));
  int[] imageRGB = image.getRGB(0, 0, image.getWidth(null), image.getHeight(null), null, 0, image.getWidth(null));

  int encodeByteMask = (int)(Math.pow(2, encodeBits)) - 1 << (8 - encodeBits);
  int encodeMask = (encodeByteMask << 24) | (encodeByteMask << 16) | (encodeByteMask << 8) | encodeByteMask;

  int decodeByteMask = ~(encodeByteMask >>> (8 - encodeBits)) & 0xFF;
  int hostMask = (decodeByteMask << 24) | (decodeByteMask << 16) | (decodeByteMask << 8) | decodeByteMask;

  for (int i = 0; i < imageRGB.length; i++)
  {
   int encodeData = (encodeRGB[i] & encodeMask) >>> (8 - encodeBits);
   imageRGB[i] = (imageRGB[i] & hostMask) | (encodeData & ~hostMask);
  }

  image.setRGB(0, 0, image.getWidth(null), image.getHeight(null), imageRGB, 0, image.getWidth(null));
 }

 public Image getImage()
 {
  return image;
 }

 public Steganography(BufferedImage image)
 {
  this.image = image;
 }
 public Steganography(){}
}
