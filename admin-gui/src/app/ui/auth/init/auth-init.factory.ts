import {AuthService} from '../../auth.service';

export function getAuthentication(authService: AuthService) {
  return () => {
    return authService.getAuthentication()
      .toPromise()
      .then((resp) => {
        console.log('Initialization function getAuthentication(): ', resp);
      }).catch((error) => {
        console.log('Initialization function getAuthentication(): ', error);
      });
  };
}

export function tryGetSettings(authService: AuthService) {
  return () => {
    return authService.tryGetSettings()
      .toPromise()
      .then((resp) => {
        console.log('Initialization function tryGetSettings(): ', resp);
      }).catch((error) => {
        console.log('Initialization function tryGetSettings(): ', error);
      });
  };
}


