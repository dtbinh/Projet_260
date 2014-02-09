package MainPackage.Agents;

import MainPackage.World;


public abstract class RandomWalker extends Agent {

	public RandomWalker( int __x, int __y, World __w )
	{
		super(__x,__y,__w);
	}
	
	public void step( )
	{
		// met a jour: (1) la couleur du sol (2) l'orientation de l'agent
		
//		int cellColor[] = _world.getCellState(_x, _y);
//		
//		if ( Math.random() > 0.5 ) // au hasard
//		{
//			cellColor[redId]   = 0;
//			cellColor[greenId] = 0;
//			cellColor[blueId]  = 0;
//		}
//		else
//		{
//			cellColor[redId]   = 255;
//			cellColor[greenId] = 255;
//			cellColor[blueId]  = 255;			
//		}
//
//		_world.setCellState(_x, _y, cellColor);

                if (Math.random() > 0.5) // au hasard
                {
                    _objectif[0]+=(int)(Math.random()*3)-1;
                } else {
                    _objectif[1]+=(int)(Math.random()*3)-1;
                }
                move();
	}
	
}
