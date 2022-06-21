import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.*;

import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;


public class MusicBox extends JFrame implements Runnable, ActionListener, AdjustmentListener
{
	JToggleButton[][]button;
	JScrollPane buttonPane;
	JScrollBar tempoBar;
	JMenuBar menuBar;
	JMenu file, instrumentMenu, songMenu;
	JMenuItem save, load;
	JMenuItem[] instrumentItems, songs;
	JButton stopPlay, clear, add, reduce, rando, selectall, typscale;
	JFileChooser fileChooser;
	JPanel buttonPanel, labelPanel, tempoPanel, menuButtonPanel;
	JLabel tempoLabel;
	boolean notStopped = true;
	JFrame frame = new JFrame();
	String[] clipNames;
	Clip[] clip;
	int tempo;
	String ans;
	int ans2;
	boolean playing = false;
	int row =0, col=0;
	Thread timing;
	Font font = new Font("Times New Roman",Font.PLAIN,10);
	String[] instrumentNames = {"Bell","Piano"};
	String[] songarr = {"Minuetto","Heman","On Melancholy Hill"};
	String[] songloc = {"Luigi Boccherini - Minuetto.txt","Heman.txt","On Melancholy Hill.txt"};
	public MusicBox()
	{
		this.setSize(1000,800);
		
	  clipNames=new String[]{"C0","B1","ASharp1","A1","GSharp1","G1","FSharp1","F1","E1","DSharp1","D1","CSharp1","C1","B2","ASharp2","A2","GSharp2","G2","FSharp2","F2","E2","DSharp2","D2","CSharp2","C2","B3","ASharp3","A3","GSharp3","G3","FSharp3","F3","E3","DSharp3","D3","CSharp3","C3"};
	  clip=new Clip[clipNames.length];
	  String initInstrument = instrumentNames[0];
      try {
         for(int x=0;x<clipNames.length;x++)
         {
         	URL url = this.getClass().getClassLoader().getResource(initInstrument+" - "+clipNames[x]+".wav");
         	AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
         	clip[x] = AudioSystem.getClip();
         	clip[x].open(audioIn);
		}

      } catch (UnsupportedAudioFileException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (LineUnavailableException e) {
         e.printStackTrace();
      }
		button = new JToggleButton[37][60];

      buttonPanel = new JPanel();
      buttonPanel.setLayout(new GridLayout(button.length,button[0].length,2,5));
      for(int r = 0; r < button.length;r++) {
    	  String name = clipNames[r].replaceAll("Sharp","#");
    	  for(int c = 0; c < button[0].length; c++) {
    		  button[r][c] = new JToggleButton();
    		  button[r][c].setFont(font);
    		  button[r][c].setText(name);
    		  button[r][c].setPreferredSize(new Dimension(30,30));
    		  button[r][c].setMargin(new Insets(0,0,0,0));
    		  buttonPanel.add(button[r][c]);
    	  }
      }
      tempoBar = new JScrollBar(JScrollBar.HORIZONTAL,200,0,50,500);
      tempoBar.addAdjustmentListener(this);
      tempo = tempoBar.getValue();
      tempoLabel = new JLabel(String.format("%s%6s","Tempo: ",tempo));
      tempoPanel = new JPanel(new BorderLayout());
      tempoPanel.add(tempoLabel,BorderLayout.WEST);
      tempoPanel.add(tempoBar,BorderLayout.CENTER);

      String currDir = System.getProperty("user.dir");
      fileChooser = new JFileChooser(currDir);
      
      menuBar = new JMenuBar();
      menuBar.setLayout(new GridLayout(1,3));
      file = new JMenu("File");
      save = new JMenuItem("Save");
      load = new JMenuItem("Load");
   
      file.add(save);
      file.add(load);
      save.addActionListener(this);
      load.addActionListener(this);
      
      
      instrumentMenu = new JMenu("Instruments");
      instrumentItems = new JMenuItem[instrumentNames.length];
      
      songMenu = new JMenu("Songs");
      songs = new JMenuItem[songarr.length];
      for(int x=0; x< instrumentNames.length; x++) {
    	  instrumentItems[x] = new JMenuItem(instrumentNames[x]);
    	  instrumentItems[x].addActionListener(this);
    	  instrumentMenu.add(instrumentItems[x]);
      }
      for(int x=0; x< songarr.length; x++) {
    	  songs[x] = new JMenuItem(songarr[x]);
    	  songs[x].addActionListener(this);
    	  songMenu.add(songs[x]);
      }
      menuBar.add(file);
      menuBar.add(songMenu);

      menuBar.add(instrumentMenu);

     
      menuButtonPanel = new JPanel();
      menuButtonPanel.setLayout(new GridLayout(1,4));
      stopPlay = new JButton("Play");
      stopPlay.addActionListener(this);
      menuButtonPanel.add(stopPlay);
      
      clear = new JButton("PL");
      clear.addActionListener(this);
      
      rando = new JButton("RB");
      rando.addActionListener(this);
      
      add = new JButton("AC");
      add.addActionListener(this);
      
      reduce = new JButton("RC");
      reduce.addActionListener(this);
      
      selectall = new JButton("SAB");
      selectall.addActionListener(this);
      
      typscale = new JButton("TS");
      typscale.addActionListener(this);
      
      menuButtonPanel.add(clear);
      menuButtonPanel.add(add);
      menuButtonPanel.add(rando);
      menuButtonPanel.add(reduce);
      menuButtonPanel.add(selectall);
      menuButtonPanel.add(typscale);

      	menuBar.add(menuButtonPanel, BorderLayout.EAST);
		buttonPane = new JScrollPane(buttonPanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.add(buttonPane,BorderLayout.CENTER);
		this.add(tempoPanel,BorderLayout.SOUTH);
		this.add(menuBar, BorderLayout.NORTH);
		
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		timing = new Thread(this);
		timing.start();
	}

	public void run()
	{
		do
		{
			try
			{
				if(!playing) {
					timing.sleep(0);
					
				}else {
						for(int r = 0; r < button.length;r++) {
							if(button[r][col].isSelected()) {
								clip[r].start();
								button[r][col].setForeground(Color.YELLOW);
							}
						}
					timing.sleep(tempo);
					for(int r = 0; r < button.length; r++) {
						if(button[r][col].isSelected()) {
							clip[r].stop();
							clip[r].setFramePosition(0);
							button[r][col].setForeground(Color.BLACK);
						}
					}
					col++;
					if(col == button[0].length)
						col = 0;
			
			}
		}
		
		catch(InterruptedException e) {
		}
		}
			while(notStopped);
	}
	

	public static void main(String args[])
	{
		MusicBox app=new MusicBox();

	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
			tempo = tempoBar.getValue();
			tempoLabel.setText(String.format("%s%6s","Tempo: ",tempo));
	}
	 public void setNotes(Character[][] notes) {
		 buttonPanel=new JPanel();
		 button = new JToggleButton[37][notes[0].length];
		 buttonPanel.setLayout(new GridLayout(button.length,button[0].length));
		 for(int r = 0; r <button.length; r++) {
		 String name = clipNames[r].replaceAll("Sharp", "#");
		 for(int c = 0; c < button[0].length; c++) {
		 button[r][c] = new JToggleButton();
		 button[r][c].setFont(font);
		 button[r][c].setText(name);
		 button[r][c].setPreferredSize(new Dimension(30,30));
		 button[r][c].setMargin(new Insets(0,0,0,0));
		 buttonPanel.add(button[r][c]);

		 }
		 }
		 this.remove(buttonPane);
		 buttonPane = new JScrollPane(buttonPanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		 this.add(buttonPane,BorderLayout.CENTER);

		 for(int r= 0; r < button.length; r++) {
		 for(int c = 0; c < button[0].length; c++) {
		 try {
		 if(notes[r][c]=='x')
		 button[r][c].setSelected(true);
		 else button[r][c].setSelected(false);
		 }catch(NullPointerException npe) {}
		 catch(ArrayIndexOutOfBoundsException ae) {}
		 }
		 }
		 this.revalidate();
		 }	public void saveSong() {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("*.txt",".txt");
		fileChooser.setFileFilter(filter);
		if(fileChooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			System.out.println(file.getPath());
			try {
				String st = file.getAbsolutePath();
				if(st.indexOf(".txt")>=0)
					st = st.substring(0,st.length()-4);
				String output = "";
				String[] noteNames = {" ","c ","b ","a-","a ","g-","g ","f-","f ","e ","d-","d ","c-","c ","b ","a-","a ","g-","g ","f-","f ","e ","d-","d ","c-","c ","b ","a-","a ","g-","g ","f-","f ","e ","d-","d ","c-"};
				
						for(int r= 0; r < button.length; r++) {
							if(r==0) {
								output+=tempo;
								for(int x = 0; x<button[0].length;x++)
									output+=" ";
							}else {
								output+=noteNames[r];
								for(int c = 0; c<button[0].length;c++) {
									if(button[r-1][c].isSelected())
										output+='x';
									else
										output+='-';
								}
							}
							output+="\n";
						}
				BufferedWriter outputStream = null;
				outputStream = new BufferedWriter(new FileWriter(st+".txt"));
				outputStream.write(output);
				outputStream.close();
			}catch(IOException exc) {
				
			}
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()== stopPlay) {
			playing = !playing;
			if(!playing)
				stopPlay.setText("Play");
			else
				stopPlay.setText("Stop");
		}
		if(e.getSource() == load) {
			int returnVal = fileChooser.showOpenDialog(this);
			
			if(returnVal==JFileChooser.APPROVE_OPTION) {
				try {
					File loadFile = fileChooser.getSelectedFile();
					BufferedReader input = new BufferedReader(new FileReader(loadFile));
					String temp;
					temp = input.readLine();
					tempo = Integer.parseInt(temp.substring(0,3));
					tempoBar.setValue(tempo);
					Character[][]song = new Character[button.length][temp.length()-2];
					int r = 0;
					while((temp=input.readLine())!=null) {
						for(int c = 2; c <song[0].length;c++) {
							song[r][c-2] = temp.charAt(c);
						}
						r++;
					}
					setNotes(song);
				}catch(IOException ee) {
				}
			}
		}
		if(e.getSource() == save) {
			saveSong();
		}
		if(e.getSource()==clear) {
			for(int i = 0; i < button.length; i++) {
				for(int j = 0; j < button[0].length; j++) {
					button[i][j].setSelected(false);
				}
			}
		
		playing = false;
		col=0;
		stopPlay.setText("Play");
		}
		
		if(e.getSource() ==rando) {
			for(int i = 0; i < button.length; i++) {
				int randrow = (int)(Math.random()*37);
				int randcol = (int)(Math.random()*button[0].length);
				for(int j = 0; j < button[0].length; j++) {
					button[randrow][randcol].setSelected(true);
				}
			}
			
		}
		if(e.getSource()==add) {
			int currentcol = button[0].length+1;
			this.remove(buttonPane);
			buttonPanel = new JPanel();
			button = new JToggleButton[37][currentcol];
			buttonPanel.setLayout(new GridLayout(button.length,currentcol));
			for(int r = 0; r <button.length; r++) {
				String name = clipNames[r].replaceAll("Sharp", "#");
				for(int c = 0; c < currentcol; c++) {
					button[r][c] = new JToggleButton();
					button[r][c].setFont(font);
					button[r][c].setText(name);
					button[r][c].setPreferredSize(new Dimension(30,30));
					button[r][c].setMargin(new Insets(0,0,0,0));
					buttonPanel.add(button[r][c]);
					
				}
			}
			buttonPane.remove(buttonPanel);
			buttonPane = new JScrollPane(buttonPanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			this.add(buttonPane,BorderLayout.CENTER);
			this.revalidate();
		}
		if(e.getSource()==reduce) {
			int currentcol = button[0].length-1;
			this.remove(buttonPane);
			buttonPanel = new JPanel();
			button = new JToggleButton[37][currentcol];
			buttonPanel.setLayout(new GridLayout(button.length,currentcol));
			for(int r = 0; r <button.length; r++) {
				String name = clipNames[r].replaceAll("Sharp", "#");
				for(int c = 0; c < currentcol; c++) {
					button[r][c] = new JToggleButton();
					button[r][c].setFont(font);
					button[r][c].setText(name);
					button[r][c].setPreferredSize(new Dimension(30,30));
					button[r][c].setMargin(new Insets(0,0,0,0));
					buttonPanel.add(button[r][c]);
					
				}
			}
			buttonPane.remove(buttonPanel);
			buttonPane = new JScrollPane(buttonPanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			this.add(buttonPane,BorderLayout.CENTER);
			this.revalidate();
		}
		if(e.getSource()==selectall) {
			for(int x = 0; x < button.length; x++) {
				for(int y= 0; y< button[0].length;y++)
					button[x][y].setSelected(true);
			}
		}
		
		if(e.getSource()==typscale) {
			for(int i = 0; i < button.length; i++) {
					button[i][i+1].setSelected(true);
			}
		}
		
		for(int y = 0; y < songarr.length; y++) {
			if(e.getSource()==songs[y]) {
				String title = songloc[y];
				File fileName = new File(title);
				try {
				BufferedReader input = new BufferedReader(new FileReader(fileName));
				String temp;
				temp = input.readLine();
				tempo = Integer.parseInt(temp.substring(0,3));
				tempoBar.setValue(tempo);
				Character[][]song = new Character[button.length][temp.length()-2];
				int r = 0;
				while((temp=input.readLine())!=null) {
					for(int c = 2; c <song[0].length;c++) {
						song[r][c-2] = temp.charAt(c);
					}
					r++;
				}
				setNotes(song);
				}catch(IOException eee) {
				}
				col=0;
				playing = false;
				stopPlay.setText("Play");
			}
			}
			for(int y = 0; y < instrumentItems.length; y++) {
				if(e.getSource()==instrumentItems[y]) {
					String selectedInstrument = instrumentNames[y];
					try {
						for(int x = 0; x < clipNames.length; x++) {
				         	URL url = this.getClass().getClassLoader().getResource(selectedInstrument+" - "+clipNames[x]+".wav");
				         	AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
				         	clip[x] = AudioSystem.getClip();
				         	clip[x].open(audioIn);
						}
					
					} catch (UnsupportedAudioFileException ee) {
				         ee.printStackTrace();
				      } catch (IOException ee) {
				         ee.printStackTrace();
				      } catch (LineUnavailableException ee) {
				         ee.printStackTrace();
				      }
					col=0;
					playing = false;
					stopPlay.setText("Play");
				}
			}		
			
		
	}
}