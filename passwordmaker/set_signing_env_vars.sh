# this file must not be ran as a separate process in order to modify your environment.
# intead use this file at the CLI by doing: source set_signing_env_vars.sh
echo -n "Keystore file: "
read PASSWORDMAKER_KEYSTORE_FILE
echo -n "Keystore password: "
read -s PASSWORDMAKER_KEYSTORE_PASSWORD
echo -n "\nKey alias: "
read PASSWORDMAKER_KEYSTORE_KEY_ALIAS
echo -n "Key password: "
read -s PASSWORDMAKER_KEYSTORE_KEY_PASSWORD
echo

export PASSWORDMAKER_KEYSTORE_FILE
export PASSWORDMAKER_KEYSTORE_PASSWORD
export PASSWORDMAKER_KEYSTORE_KEY_ALIAS
export PASSWORDMAKER_KEYSTORE_KEY_PASSWORD
