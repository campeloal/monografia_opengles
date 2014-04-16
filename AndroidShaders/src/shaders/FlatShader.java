package shaders;

import graphics.shaders.R;

public class FlatShader extends Shader{

	public FlatShader()
	{
		super.vID = R.raw.flat_vs;
		super.fID = R.raw.flat_ps;
	}
	

}
