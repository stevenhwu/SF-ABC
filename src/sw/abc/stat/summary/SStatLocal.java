package sw.abc.stat.summary;

import java.util.Arrays;

public class SStatLocal extends AbstractSummaryStat  {

	public SStatLocal() {

	}


	public double calStatMu(double[]... par) {
//		0.00375 0.00515 0.00655 0.00795 0.00935
//		0.01075 0.01215 0.01355 0.01495
		double stat = 0;
		double p = par[3][0]; 
		if( p<0.00515) {//1
			stat =  0.00524542347311207*1 +0.00686042174114125*par[0][0] -0.00237529393790472*par[0][1] -0.00588850346794428*par[0][2] -1.93830152807318e-05*par[1][0] -3.04196214994397e-05*par[1][1] -0.00286750940300701*par[2][0] +0.000416067178852737*par[2][1] ; 
		
		}else if(p>= 0.00515 && p<0.00655 ){//2
			stat =  0.00628391221678546*1 +0.00426898123959966*par[0][0] -0.000809678939870065*par[0][1] -0.00368813213245557*par[0][2] -1.07899059532387e-05*par[1][0] -1.87535218126815e-05*par[1][1] -0.00326204714599984*par[2][0] +0.000477271101888852*par[2][1] ;
				
		}else if(p>= 0.00655 && p<0.00795 ){//3
			stat =  0.00750944914877382*1 +0.00285869240618434*par[0][0] -0.000804842016443862*par[0][1] -0.00168144487611208*par[0][2] -7.57693940703929e-06*par[1][0] -1.17649127486958e-05*par[1][1] -0.00153100390817359*par[2][0] -0.00154247724880684*par[2][1] ; 
				
		}else if(p>= 0.00795 && p<0.00935 ){//4
			 stat =  0.0088144174002406*1 +0.00231633209187272*par[0][0] -0.000405294053663291*par[0][1] -0.00112631280142716*par[0][2] -5.04054714466616e-06*par[1][0] -8.82145390611524e-06*par[1][1] -0.0017651130762497*par[2][0] -0.00195625417815107*par[2][1] ;   
		
		}else if(p>= 0.00935 && p< 0.01075){//5
			 stat =  0.0101866041649257*1 +0.00134611933231587*par[0][0] -0.000193152051624846*par[0][1] -0.000769633092695518*par[0][2] -3.17672506651983e-06*par[1][0] -8.6471659180905e-06*par[1][1] -0.00133394019300905*par[2][0] -0.000991089334649562*par[2][1] ; 
		
		}else if(p>= 0.01075 && p< 0.01215){//6
			 stat =  0.0115388075420261*1 +0.00145979530178001*par[0][0] -0.00021781061519938*par[0][1] -0.000753856024879868*par[0][2] -2.92181752457325e-06*par[1][0] -5.84859303157301e-06*par[1][1] -0.00148817699087713*par[2][0] -0.000847578515454586*par[2][1] ; 
		
		}else if(p>= 0.01215 && p< 0.01355){//7
			stat =  0.0129209893788155*1 +0.00113517991114697*par[0][0] -0.000357104999297457*par[0][1] -0.000204681379387578*par[0][2] -4.05298221363775e-06*par[1][0] -3.14763193905732e-06*par[1][1] -0.000851015162722381*par[2][0] -0.00185785893402074*par[2][1] ;
		
		}else if(p>= 0.01355 ){//8
			 stat =  0.0143202583585914*1 +0.000793998567053961*par[0][0] -3.07814736068624e-05*par[0][1] -0.000346482591244473*par[0][2] -1.56444013126434e-06*par[1][0] -6.03048695938653e-06*par[1][1] -0.00106778873627136*par[2][0] -0.00103534321525332*par[2][1] ;

		}		

		return stat;

	}

	public double calStatTheta(double[]... par) {

		double stat = 0;
		double p = par[3][1]; 
		
		if( p<1500) {//1
			stat =  2019.11699211602*1 -2792.0037187935*par[0][0] -1257.92790781089*par[0][1] +1038.90212109711*par[0][2] -26.7011858079413*par[1][0] -17.3503073181871*par[1][1] +2573.40736545765*par[2][0] -253.902170831096*par[2][1] ;
				
		}
		else if(p>= 1500 && p<2000 ){//2
			stat =  2096.43481888334*1 -1839.26964401111*par[0][0] +384.987417847857*par[0][1] +1004.16352671028*par[0][2] -12.8842915342597*par[1][0] -8.67545899281236*par[1][1] -300.447417288692*par[2][0] -728.786241210536*par[2][1] ;
			 
		}
		else if(p>= 2000 && p<2500 ){//3
				stat =  2444.33069201684*1 -1385.22062875352*par[0][0] +261.835066776197*par[0][1] +1104.80662305256*par[0][2] -9.04895802865429*par[1][0] -4.16665426615495*par[1][1] +167.531388361895*par[2][0] -1197.23247937915*par[2][1] ;
			
				}
		else if(p>= 2500 && p<3000 ){//4
			stat =  2889.50428869391*1 -928.714004997533*par[0][0] +236.695788075359*par[0][1] +700.284212078222*par[0][2] -6.32010566536995*par[1][0] -3.77590287992317*par[1][1] -54.7897488170051*par[2][0] -878.03741520075*par[2][1] ; 
		
		}
		else if(p>= 3000 && p<3500 ){//5
			stat =  3345.52416726993*1 -673.669751892263*par[0][0] +296.649379677753*par[0][1] +464.217931587694*par[0][2] -4.43978955127917*par[1][0] -2.94562251439087*par[1][1] -288.49182577887*par[2][0] -594.668746153456*par[2][1] ; 
				
		}
		else if( p>= 3500 && p<4000){//6
			stat =  3812.87879044046*1 -588.225114994959*par[0][0] +354.47001476923*par[0][1] +391.88762263959*par[0][2] -3.23106618833932*par[1][0] -2.20615131868256*par[1][1] -419.370628808976*par[2][0] -396.834787723538*par[2][1] ;
				
		}else if( p>= 4000 && p<4500){//7
			stat =  4295.36250085015*1 -556.166647942306*par[0][0] +239.267790562148*par[0][1] +418.153894922554*par[0][2] -2.93534583172917*par[1][0] -1.40858805203883*par[1][1] -71.4622594992595*par[2][0] -400.59027383876*par[2][1] ; 
				
		}else if( p>= 4500 ){ //8
			stat =  4796.59143759889*1 -380.901103033455*par[0][0] +250.935147640745*par[0][1] +205.103489826361*par[0][2] -2.15072111610665*par[1][0] -2.2389803754914*par[1][1] -331.163075262494*par[2][0] -241.498532990633*par[2][1] ;

		}
		 	 		
			
		
			return stat;
	}


}